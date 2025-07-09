package com.auction.auction.service;

import com.auction.auction.dto.ProductRequest;
import com.auction.auction.model.Product;
import com.auction.auction.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(ProductRequest productRequest, String ownerUsername) {
        // ✅ Validaciones de negocio
        validateProductRequest(productRequest);

        Product product = new Product();
        product.setNombre(productRequest.getNombre());
        product.setDescripcion(productRequest.getDescripcion());
        product.setCategoria(productRequest.getCategoria());
        product.setPrecioBase(productRequest.getPrecioBase());
        product.setImagenes(productRequest.getImagenes());
        product.setOwnerUsername(ownerUsername); // ✅ Multi-tenant
        product.setEstado("disponible");

        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByOwner(String ownerUsername) {
        // ✅ Multi-tenant: Solo productos del moderador
        return productRepository.findByOwnerUsername(ownerUsername);
    }

    @Override
    public Product getProductByIdAndOwner(Long id, String ownerUsername) {
        // ✅ Multi-tenant: Solo si pertenece al moderador
        return productRepository.findByIdAndOwnerUsername(id, ownerUsername).orElse(null);
    }

    @Override
    public Product updateProduct(Long id, ProductRequest productRequest, String ownerUsername) {
        // ✅ Validaciones de negocio
        validateProductRequest(productRequest);

        Optional<Product> productOpt = productRepository.findByIdAndOwnerUsername(id, ownerUsername);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // ✅ Verificar que el producto puede ser modificado
            if ("en_subasta".equals(product.getEstado())) {
                throw new RuntimeException("No se puede modificar un producto que está en subasta activa");
            }

            product.setNombre(productRequest.getNombre());
            product.setDescripcion(productRequest.getDescripcion());
            product.setCategoria(productRequest.getCategoria());
            product.setPrecioBase(productRequest.getPrecioBase());
            product.setImagenes(productRequest.getImagenes());

            return productRepository.save(product);
        }
        return null; // ✅ 404 si no pertenece al moderador
    }

    @Override
    public void deleteProduct(Long id, String ownerUsername) {
        Optional<Product> productOpt = productRepository.findByIdAndOwnerUsername(id, ownerUsername);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // ✅ Verificar que el producto puede ser eliminado
            if ("en_subasta".equals(product.getEstado())) {
                throw new RuntimeException("No se puede eliminar un producto que está en subasta activa");
            }

            productRepository.deleteById(id);
        }
        // ✅ Silencioso si no pertenece al moderador (seguridad)
    }

    @Override
    public Page<Product> getProductsByOwnerPaginated(String ownerUsername, Pageable pageable) {
        return productRepository.findByOwnerUsername(ownerUsername, pageable);
    }

    @Override
    public Page<Product> getProductsByOwnerAndCategory(String ownerUsername, String categoria, Pageable pageable) {
        return productRepository.findByOwnerUsernameAndCategoria(ownerUsername, categoria, pageable);
    }

    @Override
    public Page<Product> searchProductsByOwner(String ownerUsername, String searchTerm, Pageable pageable) {
        return productRepository.findByOwnerUsernameAndNombreContainingIgnoreCase(ownerUsername, searchTerm, pageable);
    }

    @Override
    public List<Product> getProductsByOwnerAndStatus(String ownerUsername, String estado) {
        return productRepository.findByOwnerUsernameAndEstado(ownerUsername, estado);
    }

    @Override
    public boolean canProductBeUsedInAuction(Long productId, String ownerUsername) {
        Optional<Product> productOpt = productRepository.findByIdAndOwnerUsername(productId, ownerUsername);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return "disponible".equals(product.getEstado());
        }
        return false;
    }

    @Override
    public boolean updateProductStatus(Long productId, String newStatus, String ownerUsername) {
        Optional<Product> productOpt = productRepository.findByIdAndOwnerUsername(productId, ownerUsername);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setEstado(newStatus);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public long countProductsByOwner(String ownerUsername) {
        return productRepository.findByOwnerUsername(ownerUsername).size();
    }

    @Override
    public long countProductsByOwnerAndStatus(String ownerUsername, String estado) {
        return productRepository.findByOwnerUsernameAndEstado(ownerUsername, estado).size();
    }

    // ✅ Metodo privado para validaciones
    private void validateProductRequest(ProductRequest productRequest) {
        if (productRequest.getNombre() == null || productRequest.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (productRequest.getDescripcion() == null || productRequest.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria");
        }

        if (productRequest.getPrecioBase() == null || productRequest.getPrecioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");
        }

        if (productRequest.getCategoria() == null || productRequest.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria");
        }

        // ✅ Validar categorías permitidas
        List<String> categoriasPermitidas = List.of("electronica", "hogar", "deportes", "ropa", "libros", "arte", "otros");
        if (!categoriasPermitidas.contains(productRequest.getCategoria().toLowerCase())) {
            throw new IllegalArgumentException("Categoría no válida. Categorías permitidas: " + String.join(", ", categoriasPermitidas));
        }
    }
}