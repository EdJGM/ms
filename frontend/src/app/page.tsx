'use client';

import React, { useEffect, useState } from 'react';
import Link from 'next/link';
import { useAuthStore } from '@/store/authStore';
import { useAuctionStore } from '@/store/auctionStore';
import AuctionCard from '@/components/auctions/AuctionCard';
import { 
  ClockIcon, 
  UserGroupIcon, 
  ShieldCheckIcon,
  TrophyIcon,
  ArrowRightIcon
} from '@heroicons/react/24/outline';

export default function HomePage() {
  const { isAuthenticated, user } = useAuthStore();
  const { auctions, loadAuctions, isLoading } = useAuctionStore();
  const [stats, setStats] = useState({
    activeAuctions: 0,
    totalUsers: 0,
    completedAuctions: 0
  });

  useEffect(() => {
    loadAuctions();
    // Simulate loading stats
    setStats({
      activeAuctions: 127,
      totalUsers: 2843,
      completedAuctions: 1562
    });
  }, [loadAuctions]);

  const featuredAuctions = auctions.slice(0, 6);

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              Descubre las Mejores
              <span className="block text-yellow-300">Subastas en Línea</span>
            </h1>
            <p className="text-xl md:text-2xl text-primary-100 mb-8 max-w-3xl mx-auto">
              Únete a miles de usuarios que compran y venden productos únicos 
              en nuestra plataforma segura y confiable.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              {isAuthenticated ? (
                <Link
                  href="/auctions"
                  className="inline-flex items-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-primary-600 bg-white hover:bg-gray-50 transition-colors duration-200"
                >
                  Ver Subastas
                  <ArrowRightIcon className="ml-2 h-5 w-5" />
                </Link>
              ) : (
                <>
                  <Link
                    href="/auth/register"
                    className="inline-flex items-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-primary-600 bg-white hover:bg-gray-50 transition-colors duration-200"
                  >
                    Comenzar Ahora
                    <ArrowRightIcon className="ml-2 h-5 w-5" />
                  </Link>
                  <Link
                    href="/auctions"
                    className="inline-flex items-center px-8 py-3 border-2 border-white text-base font-medium rounded-md text-white hover:bg-white hover:text-primary-600 transition-colors duration-200"
                  >
                    Explorar Subastas
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Welcome Section for Authenticated Users */}
      {isAuthenticated && (
        <section className="bg-primary-50 border-b border-primary-200">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div className="text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                ¡Bienvenido de vuelta, {user?.username}!
              </h2>
              <p className="text-gray-600">
                Descubre nuevas oportunidades y continúa participando en subastas emocionantes.
              </p>
            </div>
          </div>
        </section>
      )}

      {/* Stats Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="flex justify-center mb-4">
                <ClockIcon className="h-12 w-12 text-auction-active" />
              </div>
              <div className="text-3xl font-bold text-gray-900 mb-2">{stats.activeAuctions}</div>
              <div className="text-gray-600">Subastas Activas</div>
            </div>
            <div className="text-center">
              <div className="flex justify-center mb-4">
                <UserGroupIcon className="h-12 w-12 text-primary-600" />
              </div>
              <div className="text-3xl font-bold text-gray-900 mb-2">{stats.totalUsers.toLocaleString()}</div>
              <div className="text-gray-600">Usuarios Registrados</div>
            </div>
            <div className="text-center">
              <div className="flex justify-center mb-4">
                <TrophyIcon className="h-12 w-12 text-auction-ending" />
              </div>
              <div className="text-3xl font-bold text-gray-900 mb-2">{stats.completedAuctions.toLocaleString()}</div>
              <div className="text-gray-600">Subastas Completadas</div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Auctions */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Subastas Destacadas
            </h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Descubre las subastas más populares y emocionantes del momento. 
              No te pierdas estas oportunidades únicas.
            </p>
          </div>

          {isLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="card animate-pulse">
                  <div className="h-48 bg-gray-300 rounded-lg mb-4"></div>
                  <div className="h-4 bg-gray-300 rounded w-3/4 mb-2"></div>
                  <div className="h-4 bg-gray-300 rounded w-1/2 mb-4"></div>
                  <div className="h-8 bg-gray-300 rounded"></div>
                </div>
              ))}
            </div>
          ) : featuredAuctions.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {featuredAuctions.map((auction) => (
                <AuctionCard key={auction.auctionId} auction={auction} />
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <ClockIcon className="mx-auto h-12 w-12 text-gray-400 mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                No hay subastas disponibles
              </h3>
              <p className="text-gray-500">
                Regresa más tarde para ver nuevas subastas emocionantes.
              </p>
            </div>
          )}

          <div className="text-center mt-12">
            <Link
              href="/auctions"
              className="btn-primary inline-flex items-center"
            >
              Ver Todas las Subastas
              <ArrowRightIcon className="ml-2 h-5 w-5" />
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              ¿Por qué elegir SubastasApp?
            </h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Ofrecemos la experiencia de subastas más segura, confiable y emocionante del mercado.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="bg-primary-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6">
                <ShieldCheckIcon className="h-8 w-8 text-primary-600" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Seguridad Garantizada
              </h3>
              <p className="text-gray-600">
                Transacciones seguras con autenticación avanzada y protección de datos.
              </p>
            </div>

            <div className="text-center">
              <div className="bg-auction-active/10 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6">
                <ClockIcon className="h-8 w-8 text-auction-active" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Subastas en Tiempo Real
              </h3>
              <p className="text-gray-600">
                Participa en subastas en vivo con actualizaciones instantáneas y notificaciones.
              </p>
            </div>

            <div className="text-center">
              <div className="bg-auction-ending/10 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-6">
                <UserGroupIcon className="h-8 w-8 text-auction-ending" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-4">
                Comunidad Activa
              </h3>
              <p className="text-gray-600">
                Únete a miles de usuarios que compran y venden productos únicos diariamente.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      {!isAuthenticated && (
        <section className="bg-gray-900 text-white py-16">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl font-bold mb-4">
              ¿Listo para comenzar?
            </h2>
            <p className="text-xl text-gray-300 mb-8 max-w-2xl mx-auto">
              Regístrate ahora y descubre un mundo de oportunidades únicas en nuestras subastas.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                href="/auth/register"
                className="btn-primary bg-white text-gray-900 hover:bg-gray-100"
              >
                Crear Cuenta Gratis
              </Link>
              <Link
                href="/auth/login"
                className="border-2 border-white px-6 py-3 rounded-lg font-medium hover:bg-white hover:text-gray-900 transition-colors duration-200"
              >
                Iniciar Sesión
              </Link>
            </div>
          </div>
        </section>
      )}
    </div>
  );
}