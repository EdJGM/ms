'use client';

import React, { useState } from 'react';
import { ChevronDownIcon, ChevronUpIcon } from '@heroicons/react/24/outline';

interface AuctionFiltersProps {
  onFilterChange: (filters: any) => void;
  categories: string[];
}

const AuctionFilters: React.FC<AuctionFiltersProps> = ({ 
  onFilterChange, 
  categories 
}) => {
  const [selectedCategory, setSelectedCategory] = useState('Todas');
  const [selectedStatus, setSelectedStatus] = useState('todas');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });
  const [timeFilter, setTimeFilter] = useState('todas');
  const [expandedSections, setExpandedSections] = useState({
    category: true,
    status: true,
    price: false,
    time: false
  });

  const toggleSection = (section: keyof typeof expandedSections) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  const handleCategoryChange = (category: string) => {
    setSelectedCategory(category);
    applyFilters({
      categoria: category === 'Todas' ? undefined : category
    });
  };

  const handleStatusChange = (status: string) => {
    setSelectedStatus(status);
    applyFilters({
      estado: status === 'todas' ? undefined : status
    });
  };

  const handlePriceChange = (field: 'min' | 'max', value: string) => {
    const newPriceRange = { ...priceRange, [field]: value };
    setPriceRange(newPriceRange);
    
    // Only apply filter if both min and max have values or if clearing
    if ((newPriceRange.min && newPriceRange.max) || (!newPriceRange.min && !newPriceRange.max)) {
      applyFilters({
        precioMin: newPriceRange.min ? parseFloat(newPriceRange.min) : undefined,
        precioMax: newPriceRange.max ? parseFloat(newPriceRange.max) : undefined
      });
    }
  };

  const handleTimeFilterChange = (filter: string) => {
    setTimeFilter(filter);
    applyFilters({
      tiempo: filter === 'todas' ? undefined : filter
    });
  };

  const applyFilters = (newFilters: any) => {
    const filters = {
      categoria: selectedCategory === 'Todas' ? undefined : selectedCategory,
      estado: selectedStatus === 'todas' ? undefined : selectedStatus,
      precioMin: priceRange.min ? parseFloat(priceRange.min) : undefined,
      precioMax: priceRange.max ? parseFloat(priceRange.max) : undefined,
      tiempo: timeFilter === 'todas' ? undefined : timeFilter,
      ...newFilters
    };

    // Remove undefined values
    const cleanedFilters = Object.fromEntries(
      Object.entries(filters).filter(([_, value]) => value !== undefined)
    );

    onFilterChange(cleanedFilters);
  };

  const clearAllFilters = () => {
    setSelectedCategory('Todas');
    setSelectedStatus('todas');
    setPriceRange({ min: '', max: '' });
    setTimeFilter('todas');
    onFilterChange({});
  };

  const FilterSection = ({ 
    title, 
    isExpanded, 
    onToggle, 
    children 
  }: { 
    title: string; 
    isExpanded: boolean; 
    onToggle: () => void; 
    children: React.ReactNode; 
  }) => (
    <div className="border-b border-gray-200 pb-4 mb-4">
      <button
        className="flex items-center justify-between w-full text-left"
        onClick={onToggle}
      >
        <h3 className="text-sm font-medium text-gray-900">{title}</h3>
        {isExpanded ? (
          <ChevronUpIcon className="h-4 w-4 text-gray-400" />
        ) : (
          <ChevronDownIcon className="h-4 w-4 text-gray-400" />
        )}
      </button>
      {isExpanded && (
        <div className="mt-3">{children}</div>
      )}
    </div>
  );

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-lg font-semibold text-gray-900">Filtros</h2>
        <button
          onClick={clearAllFilters}
          className="text-sm text-primary-600 hover:text-primary-700"
        >
          Limpiar todo
        </button>
      </div>

      {/* Category Filter */}
      <FilterSection
        title="Categoría"
        isExpanded={expandedSections.category}
        onToggle={() => toggleSection('category')}
      >
        <div className="space-y-2">
          {categories.map((category) => (
            <label key={category} className="flex items-center">
              <input
                type="radio"
                name="category"
                value={category}
                checked={selectedCategory === category}
                onChange={() => handleCategoryChange(category)}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300"
              />
              <span className="ml-2 text-sm text-gray-700">{category}</span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Status Filter */}
      <FilterSection
        title="Estado"
        isExpanded={expandedSections.status}
        onToggle={() => toggleSection('status')}
      >
        <div className="space-y-2">
          {[
            { value: 'todas', label: 'Todas' },
            { value: 'activa', label: 'Activas' },
            { value: 'programada', label: 'Programadas' },
            { value: 'finalizada', label: 'Finalizadas' }
          ].map((status) => (
            <label key={status.value} className="flex items-center">
              <input
                type="radio"
                name="status"
                value={status.value}
                checked={selectedStatus === status.value}
                onChange={() => handleStatusChange(status.value)}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300"
              />
              <span className="ml-2 text-sm text-gray-700">{status.label}</span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Price Range Filter */}
      <FilterSection
        title="Rango de precio"
        isExpanded={expandedSections.price}
        onToggle={() => toggleSection('price')}
      >
        <div className="space-y-3">
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Precio mínimo
            </label>
            <input
              type="number"
              placeholder="0"
              value={priceRange.min}
              onChange={(e) => handlePriceChange('min', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">
              Precio máximo
            </label>
            <input
              type="number"
              placeholder="999999"
              value={priceRange.max}
              onChange={(e) => handlePriceChange('max', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-primary-500 focus:border-primary-500"
            />
          </div>
        </div>
      </FilterSection>

      {/* Time Filter */}
      <FilterSection
        title="Tiempo restante"
        isExpanded={expandedSections.time}
        onToggle={() => toggleSection('time')}
      >
        <div className="space-y-2">
          {[
            { value: 'todas', label: 'Todas' },
            { value: 'ending-soon', label: 'Terminan pronto (< 1 hora)' },
            { value: 'ending-today', label: 'Terminan hoy' },
            { value: 'ending-week', label: 'Terminan esta semana' }
          ].map((filter) => (
            <label key={filter.value} className="flex items-center">
              <input
                type="radio"
                name="timeFilter"
                value={filter.value}
                checked={timeFilter === filter.value}
                onChange={() => handleTimeFilterChange(filter.value)}
                className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300"
              />
              <span className="ml-2 text-sm text-gray-700">{filter.label}</span>
            </label>
          ))}
        </div>
      </FilterSection>
    </div>
  );
};

export default AuctionFilters;