'use client';

import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useAuctionStore } from '@/store/auctionStore';
import AuctionCard from '@/components/auctions/AuctionCard';
import AuctionFilters from '@/components/auctions/AuctionFilters';
import { 
  MagnifyingGlassIcon,
  AdjustmentsHorizontalIcon,
  XMarkIcon
} from '@heroicons/react/24/outline';

export default function AuctionsPage() {
  const searchParams = useSearchParams();
  const { auctions, loadAuctions, searchAuctions, isLoading, error, filters, setFilters } = useAuctionStore();
  
  const [searchTerm, setSearchTerm] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [sortBy, setSortBy] = useState('recent');

  const initialCategory = searchParams?.get('category');
  const initialSearch = searchParams?.get('search');

  useEffect(() => {
    if (initialCategory) {
      setFilters({ categoria: initialCategory });
    }
    if (initialSearch) {
      setSearchTerm(initialSearch);
    }
    
    if (initialSearch || initialCategory) {
      searchAuctions(initialSearch || '', initialCategory || '');
    } else {
      loadAuctions();
    }
  }, [initialCategory, initialSearch, setFilters, loadAuctions, searchAuctions]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    searchAuctions(searchTerm, filters.categoria);
  };

  const handleClearFilters = () => {
    setFilters({ page: 0, limit: 10 });
    setSearchTerm('');
    setSortBy('recent');
    loadAuctions();
  };

  const sortedAuctions = [...auctions].sort((a, b) => {
    switch (sortBy) {
      case 'price-low':
        return (a.precioActual || a.startingPrice) - (b.precioActual || b.startingPrice);
      case 'price-high':
        return (b.precioActual || b.startingPrice) - (a.precioActual || a.startingPrice);
      case 'ending-soon':
        if (!a.fechaFin || !b.fechaFin) return 0;
        return new Date(a.fechaFin).getTime() - new Date(b.fechaFin).getTime();
      case 'recent':
      default:
        if (!a.fechaInicio || !b.fechaInicio) return 0;
        return new Date(b.fechaInicio).getTime() - new Date(a.fechaInicio).getTime();
    }
  });

  const categories = [
    'Todas',
    'Electrónicos',
    'Vehículos',
    'Hogar y Jardín',
    'Ropa y Accesorios',
    'Deportes',
    'Coleccionables',
    'Arte',
    'Libros',
    'Otros'
  ];

  const activeFiltersCount = Object.keys(filters).filter(key => 
    key !== 'page' && key !== 'limit' && filters[key as keyof typeof filters]
  ).length + (searchTerm ? 1 : 0);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Subastas</h1>
              <p className="text-gray-600 mt-2">
                Descubre oportunidades únicas en nuestras subastas activas
              </p>
            </div>

            {/* Search */}
            <form onSubmit={handleSearch} className="flex-1 max-w-lg">
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="text"
                  className="input-field pl-10 pr-4"
                  placeholder="Buscar subastas..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button
                  type="submit"
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                >
                  <span className="sr-only">Buscar</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Filters - Desktop */}
          <div className="hidden lg:block w-64 flex-shrink-0">
            <AuctionFilters 
              onFilterChange={(newFilters) => {
                setFilters(newFilters);
                searchAuctions(searchTerm, newFilters.categoria);
              }}
              categories={categories}
            />
          </div>

          {/* Main Content */}
          <div className="flex-1">
            {/* Mobile Filter Toggle & Sort */}
            <div className="flex items-center justify-between mb-6">
              <button
                onClick={() => setShowFilters(!showFilters)}
                className="lg:hidden flex items-center space-x-2 px-4 py-2 bg-white border border-gray-300 rounded-lg shadow-sm hover:bg-gray-50"
              >
                <AdjustmentsHorizontalIcon className="h-5 w-5" />
                <span>Filtros</span>
                {activeFiltersCount > 0 && (
                  <span className="bg-primary-600 text-white text-xs rounded-full px-2 py-0.5">
                    {activeFiltersCount}
                  </span>
                )}
              </button>

              <div className="flex items-center space-x-4">
                {activeFiltersCount > 0 && (
                  <button
                    onClick={handleClearFilters}
                    className="flex items-center space-x-1 text-sm text-gray-600 hover:text-gray-900"
                  >
                    <XMarkIcon className="h-4 w-4" />
                    <span>Limpiar filtros</span>
                  </button>
                )}

                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="text-sm border border-gray-300 rounded-lg px-3 py-2 bg-white focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="recent">Más recientes</option>
                  <option value="ending-soon">Terminan pronto</option>
                  <option value="price-low">Precio: menor a mayor</option>
                  <option value="price-high">Precio: mayor a menor</option>
                </select>
              </div>
            </div>

            {/* Mobile Filters */}
            {showFilters && (
              <div className="lg:hidden mb-6 bg-white rounded-lg shadow-sm border p-4">
                <AuctionFilters 
                  onFilterChange={(newFilters) => {
                    setFilters(newFilters);
                    searchAuctions(searchTerm, newFilters.categoria);
                    setShowFilters(false);
                  }}
                  categories={categories}
                />
              </div>
            )}

            {/* Results Count */}
            <div className="mb-6 text-sm text-gray-600">
              {isLoading ? (
                'Cargando subastas...'
              ) : (
                `Mostrando ${sortedAuctions.length} subastas`
              )}
            </div>

            {/* Error State */}
            {error && (
              <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
                <p className="text-red-800">{error}</p>
              </div>
            )}

            {/* Auctions Grid */}
            {isLoading ? (
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                {[...Array(6)].map((_, i) => (
                  <div key={i} className="card animate-pulse">
                    <div className="h-4 bg-gray-300 rounded w-1/4 mb-4"></div>
                    <div className="h-6 bg-gray-300 rounded w-3/4 mb-2"></div>
                    <div className="h-4 bg-gray-300 rounded w-1/2 mb-4"></div>
                    <div className="space-y-2 mb-4">
                      <div className="h-4 bg-gray-300 rounded"></div>
                      <div className="h-4 bg-gray-300 rounded w-3/4"></div>
                    </div>
                    <div className="h-10 bg-gray-300 rounded"></div>
                  </div>
                ))}
              </div>
            ) : sortedAuctions.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                {sortedAuctions.map((auction) => (
                  <AuctionCard key={auction.auctionId} auction={auction} />
                ))}
              </div>
            ) : (
              <div className="text-center py-12">
                <MagnifyingGlassIcon className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                  No se encontraron subastas
                </h3>
                <p className="text-gray-500 mb-4">
                  Intenta ajustar tus filtros o buscar con diferentes términos.
                </p>
                <button
                  onClick={handleClearFilters}
                  className="btn-primary"
                >
                  Limpiar filtros
                </button>
              </div>
            )}

            {/* Load More */}
            {sortedAuctions.length > 0 && sortedAuctions.length >= (filters.limit || 10) && (
              <div className="text-center mt-12">
                <button
                  onClick={() => {
                    const newFilters = { ...filters, limit: (filters.limit || 10) + 10 };
                    setFilters(newFilters);
                    searchAuctions(searchTerm, newFilters.categoria);
                  }}
                  className="btn-secondary"
                  disabled={isLoading}
                >
                  {isLoading ? 'Cargando...' : 'Cargar más'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}