'use client';

import React from 'react';
import Link from 'next/link';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  const footerLinks = {
    product: [
      { name: 'Cómo funciona', href: '/how-it-works' },
      { name: 'Categorías', href: '/categories' },
      { name: 'Subastas populares', href: '/auctions?sort=popular' },
      { name: 'Preguntas frecuentes', href: '/faq' },
    ],
    support: [
      { name: 'Centro de ayuda', href: '/help' },
      { name: 'Contacto', href: '/contact' },
      { name: 'Estado del sistema', href: '/status' },
      { name: 'Reportar problema', href: '/report' },
    ],
    legal: [
      { name: 'Términos de uso', href: '/terms' },
      { name: 'Política de privacidad', href: '/privacy' },
      { name: 'Cookies', href: '/cookies' },
      { name: 'Aviso legal', href: '/legal' },
    ],
    social: [
      { name: 'Twitter', href: 'https://twitter.com/subastasapp' },
      { name: 'Facebook', href: 'https://facebook.com/subastasapp' },
      { name: 'Instagram', href: 'https://instagram.com/subastasapp' },
      { name: 'LinkedIn', href: 'https://linkedin.com/company/subastasapp' },
    ],
  };

  return (
    <footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand Section */}
          <div className="col-span-1 lg:col-span-2">
            <div className="flex items-center space-x-2 mb-4">
              <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-lg">S</span>
              </div>
              <span className="text-xl font-bold">SubastasApp</span>
            </div>
            <p className="text-gray-300 text-sm mb-4 max-w-md">
              La plataforma de subastas en línea más confiable. Encuentra ofertas increíbles 
              y vende tus productos de manera segura y eficiente.
            </p>
            <div className="flex space-x-4">
              {footerLinks.social.map((item) => (
                <a
                  key={item.name}
                  href={item.href}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-gray-400 hover:text-white transition-colors duration-200"
                >
                  <span className="sr-only">{item.name}</span>
                  <div className="w-6 h-6 bg-gray-700 hover:bg-primary-600 rounded transition-colors duration-200"></div>
                </a>
              ))}
            </div>
          </div>

          {/* Product Links */}
          <div>
            <h3 className="text-sm font-semibold text-gray-200 uppercase tracking-wider mb-4">
              Producto
            </h3>
            <ul className="space-y-3">
              {footerLinks.product.map((item) => (
                <li key={item.name}>
                  <Link
                    href={item.href}
                    className="text-gray-300 hover:text-white transition-colors duration-200 text-sm"
                  >
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Support Links */}
          <div>
            <h3 className="text-sm font-semibold text-gray-200 uppercase tracking-wider mb-4">
              Soporte
            </h3>
            <ul className="space-y-3">
              {footerLinks.support.map((item) => (
                <li key={item.name}>
                  <Link
                    href={item.href}
                    className="text-gray-300 hover:text-white transition-colors duration-200 text-sm"
                  >
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom Section */}
        <div className="mt-12 pt-8 border-t border-gray-800">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <div className="flex flex-wrap justify-center md:justify-start space-x-6 mb-4 md:mb-0">
              {footerLinks.legal.map((item) => (
                <Link
                  key={item.name}
                  href={item.href}
                  className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
                >
                  {item.name}
                </Link>
              ))}
            </div>
            <p className="text-gray-400 text-sm">
              © {currentYear} SubastasApp. Todos los derechos reservados.
            </p>
          </div>
        </div>

        {/* Additional Info */}
        <div className="mt-8 pt-8 border-t border-gray-800">
          <div className="text-center text-gray-400 text-xs">
            <p className="mb-2">
              SubastasApp es una plataforma segura que conecta compradores y vendedores.
            </p>
            <p>
              Desarrollado con Next.js, Spring Boot y tecnologías modernas para garantizar 
              la mejor experiencia de usuario.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;