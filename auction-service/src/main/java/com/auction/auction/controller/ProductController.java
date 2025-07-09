package com.auction.auction.controller;

import com.auction.auction.dto.ProductRequest;
import com.auction.auction.model.Product;
import com.auction.auction.security.JwtUtils;
import com.auction.auction.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductController {

    private final ProductService productService;
    private final JwtUtils jwtUtils;

    @Autowired
    public ProductController(ProductService productService, JwtUtils jwtUtils) {
        this.productService = productService;
        this.jwtUtils = jwtUtils;
    }

    //Crear producto (solo MODERADOR y ADMINISTRADOR)
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest, HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            Product product = productService.createProduct(productRequest, username);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Producto creado exitosamente",
                    "product", product
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Obtener mis productos (con paginación)
    @GetMapping
    public ResponseEntity<?> getMyProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            HttpServletRequest request) {

        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            // ✅ Configurar paginación y ordenamiento
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Product> products;

            // ✅ Aplicar filtros
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProductsByOwner(username, search, pageable);
            } else if (categoria != null && !categoria.trim().isEmpty()) {
                products = productService.getProductsByOwnerAndCategory(username, categoria, pageable);
            } else {
                products = productService.getProductsByOwnerPaginated(username, pageable);
            }

            // ✅ Filtrar por estado si se especifica
            if (estado != null && !estado.trim().isEmpty()) {
                // Para simplificar, obtenemos todos y filtramos por estado
                List<Product> filteredProducts = productService.getProductsByOwnerAndStatus(username, estado);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "products", filteredProducts,
                        "total", filteredProducts.size()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "products", products.getContent(),
                    "pagination", Map.of(
                            "page", products.getNumber(),
                            "size", products.getSize(),
                            "totalElements", products.getTotalElements(),
                            "totalPages", products.getTotalPages(),
                            "first", products.isFirst(),
                            "last", products.isLast()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Obtener producto específico (solo del moderador)
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id, HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            Product product = productService.getProductByIdAndOwner(id, username);
            if (product != null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "product", product
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "NOT_FOUND",
                        "message", "Producto no encontrado o no tienes permisos para verlo"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Actualizar producto (solo del moderador)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest, HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            Product updated = productService.updateProduct(id, productRequest, username);
            if (updated != null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Producto actualizado exitosamente",
                        "product", updated
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "NOT_FOUND",
                        "message", "Producto no encontrado o no tienes permisos para modificarlo"
                ));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "VALIDATION_ERROR",
                    "message", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "BUSINESS_RULE_VIOLATION",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Eliminar producto (solo del moderador)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            productService.deleteProduct(id, username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Producto eliminado exitosamente"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "BUSINESS_RULE_VIOLATION",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Obtener estadísticas de productos del moderador
    @GetMapping("/stats")
    public ResponseEntity<?> getProductStats(HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            long totalProducts = productService.countProductsByOwner(username);
            long availableProducts = productService.countProductsByOwnerAndStatus(username, "disponible");
            long inAuctionProducts = productService.countProductsByOwnerAndStatus(username, "en_subasta");
            long soldProducts = productService.countProductsByOwnerAndStatus(username, "vendido");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", Map.of(
                            "total", totalProducts,
                            "disponible", availableProducts,
                            "en_subasta", inAuctionProducts,
                            "vendido", soldProducts
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Verificar si un producto puede ser usado en subasta
    @GetMapping("/{id}/can-auction")
    public ResponseEntity<?> canProductBeAuctioned(@PathVariable Long id, HttpServletRequest request) {
        try {
            String username = extractAndValidateUser(request);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "UNAUTHORIZED", "message", "Token inválido o ausente"));
            }

            boolean canAuction = productService.canProductBeUsedInAuction(id, username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "canAuction", canAuction,
                    "message", canAuction ? "El producto puede ser subastado" : "El producto no está disponible para subasta"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    //Metodo helper para extraer y validar usuario
    private String extractAndValidateUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String token = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return null;
        }
        return jwtUtils.getUserNameFromJwtToken(token);
    }
}