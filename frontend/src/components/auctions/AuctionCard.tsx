'use client';

import React from 'react';
import Link from 'next/link';
import { AuctionView } from '@/types';
import { 
  ClockIcon, 
  CurrencyDollarIcon,
  UserIcon,
  EyeIcon
} from '@heroicons/react/24/outline';
import moment from 'moment';

interface AuctionCardProps {
  auction: AuctionView;
  showViewButton?: boolean;
}

const AuctionCard: React.FC<AuctionCardProps> = ({ 
  auction, 
  showViewButton = true 
}) => {
  const getStatusInfo = () => {
    switch (auction.estado) {
      case 'activa':
        return {
          color: 'auction-active',
          bgColor: 'bg-green-50',
          textColor: 'text-green-800',
          label: 'Activa'
        };
      case 'programada':
        return {
          color: 'auction-ending',
          bgColor: 'bg-yellow-50',
          textColor: 'text-yellow-800',
          label: 'Programada'
        };
      case 'finalizada':
        return {
          color: 'auction-ended',
          bgColor: 'bg-red-50',
          textColor: 'text-red-800',
          label: 'Finalizada'
        };
      default:
        return {
          color: 'gray-400',
          bgColor: 'bg-gray-50',
          textColor: 'text-gray-800',
          label: auction.estado
        };
    }
  };

  const statusInfo = getStatusInfo();

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const getTimeRemaining = () => {
    if (!auction.fechaFin) return null;
    
    const endTime = moment(auction.fechaFin);
    const now = moment();
    
    if (endTime.isBefore(now)) {
      return 'Finalizada';
    }
    
    const duration = moment.duration(endTime.diff(now));
    const days = Math.floor(duration.asDays());
    const hours = duration.hours();
    const minutes = duration.minutes();
    
    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m`;
    } else if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else {
      return `${minutes}m`;
    }
  };

  const timeRemaining = getTimeRemaining();

  return (
    <div className={`auction-card border-l-${statusInfo.color}`}>
      {/* Status Badge */}
      <div className="flex justify-between items-start mb-4">
        <span className={`status-badge ${statusInfo.bgColor} ${statusInfo.textColor}`}>
          {statusInfo.label}
        </span>
        {auction.bidCount && (
          <span className="text-xs text-gray-500 flex items-center">
            <UserIcon className="h-3 w-3 mr-1" />
            {auction.bidCount} pujas
          </span>
        )}
      </div>

      {/* Auction Info */}
      <div className="mb-4">
        <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
          {auction.description}
        </h3>
        
        <div className="flex items-center text-sm text-gray-600 mb-2">
          <UserIcon className="h-4 w-4 mr-1" />
          <span>Vendedor: {auction.ownerUsername}</span>
        </div>

        {auction.itemCategory && (
          <div className="text-sm text-gray-500 mb-3">
            Categoría: {auction.itemCategory}
          </div>
        )}
      </div>

      {/* Pricing */}
      <div className="mb-4">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm text-gray-600">Precio inicial:</span>
          <span className="text-sm font-medium">
            {formatPrice(auction.startingPrice)}
          </span>
        </div>
        
        {auction.precioActual && auction.precioActual > auction.startingPrice && (
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm text-gray-600">Puja actual:</span>
            <span className="text-lg font-bold text-auction-active flex items-center">
              <CurrencyDollarIcon className="h-4 w-4 mr-1" />
              {formatPrice(auction.precioActual)}
            </span>
          </div>
        )}
      </div>

      {/* Time Remaining */}
      {timeRemaining && auction.estado === 'activa' && (
        <div className="mb-4 p-3 bg-primary-50 rounded-lg">
          <div className="flex items-center text-sm">
            <ClockIcon className="h-4 w-4 mr-2 text-primary-600" />
            <span className="text-primary-700 font-medium">
              Tiempo restante: {timeRemaining}
            </span>
          </div>
        </div>
      )}

      {/* Start/End Dates */}
      <div className="text-xs text-gray-500 mb-4 space-y-1">
        {auction.fechaInicio && (
          <div>
            Inicio: {moment(auction.fechaInicio).format('DD/MM/YYYY HH:mm')}
          </div>
        )}
        {auction.fechaFin && (
          <div>
            Fin: {moment(auction.fechaFin).format('DD/MM/YYYY HH:mm')}
          </div>
        )}
      </div>

      {/* Action Button */}
      {showViewButton && (
        <div className="mt-auto">
          <Link
            href={`/auctions/${auction.auctionId}`}
            className="w-full btn-primary flex items-center justify-center text-sm"
          >
            <EyeIcon className="h-4 w-4 mr-2" />
            Ver Detalles
          </Link>
        </div>
      )}

      {/* Live Indicator */}
      {auction.estado === 'activa' && (
        <div className="absolute top-2 right-2">
          <div className="flex items-center space-x-1">
            <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></div>
            <span className="text-xs font-medium text-red-600">EN VIVO</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuctionCard;