'use client';

import React, { useState } from 'react';
import { Bid } from '@/types';
import { useAuthStore } from '@/store/authStore';
import { 
  TrophyIcon, 
  UserIcon, 
  ClockIcon,
  EyeIcon,
  EyeSlashIcon
} from '@heroicons/react/24/outline';
import moment from 'moment';

interface BidHistoryProps {
  bids: Bid[];
}

const BidHistory: React.FC<BidHistoryProps> = ({ bids }) => {
  const { user } = useAuthStore();
  const [showAll, setShowAll] = useState(false);
  const [hideUsernames, setHideUsernames] = useState(false);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const maskUsername = (username: string) => {
    if (username.length <= 2) return username;
    return username.charAt(0) + '*'.repeat(username.length - 2) + username.charAt(username.length - 1);
  };

  const displayedBids = showAll ? bids : bids.slice(0, 10);
  const hasMoreBids = bids.length > 10;

  if (bids.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          Historial de Pujas
        </h3>
        <div className="text-center py-8">
          <ClockIcon className="mx-auto h-8 w-8 text-gray-400 mb-3" />
          <p className="text-gray-600">
            Aún no hay pujas en esta subasta.
          </p>
          <p className="text-sm text-gray-500 mt-1">
            ¡Sé el primero en participar!
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">
          Historial de Pujas ({bids.length})
        </h3>
        
        <div className="flex items-center space-x-2">
          <button
            onClick={() => setHideUsernames(!hideUsernames)}
            className="flex items-center space-x-1 text-sm text-gray-600 hover:text-gray-900 transition-colors"
          >
            {hideUsernames ? (
              <EyeIcon className="h-4 w-4" />
            ) : (
              <EyeSlashIcon className="h-4 w-4" />
            )}
            <span>{hideUsernames ? 'Mostrar' : 'Ocultar'} usuarios</span>
          </button>
        </div>
      </div>

      <div className="space-y-3">
        {displayedBids.map((bid, index) => {
          const isWinning = index === 0;
          const isUserBid = user?.username === bid.username;
          const displayUsername = hideUsernames && !isUserBid 
            ? maskUsername(bid.username) 
            : bid.username;

          return (
            <div
              key={bid.bidId}
              className={`p-4 rounded-lg border transition-all ${
                isWinning
                  ? 'bg-green-50 border-green-200 ring-1 ring-green-200'
                  : isUserBid
                  ? 'bg-blue-50 border-blue-200'
                  : 'bg-gray-50 border-gray-200'
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  {isWinning && (
                    <TrophyIcon className="h-5 w-5 text-yellow-500" />
                  )}
                  
                  <div className="flex items-center space-x-2">
                    <UserIcon className="h-4 w-4 text-gray-400" />
                    <span className={`font-medium ${
                      isUserBid ? 'text-blue-700' : 'text-gray-900'
                    }`}>
                      {displayUsername}
                      {isUserBid && ' (Tú)'}
                    </span>
                  </div>
                </div>

                <div className="text-right">
                  <div className={`text-lg font-bold ${
                    isWinning ? 'text-green-700' : 'text-gray-900'
                  }`}>
                    {formatPrice(bid.bidPrice)}
                  </div>
                  <div className="text-xs text-gray-500">
                    {moment().format('DD/MM/YYYY HH:mm')}
                  </div>
                </div>
              </div>

              {isWinning && (
                <div className="mt-2 flex items-center text-sm text-green-700">
                  <TrophyIcon className="h-4 w-4 mr-1" />
                  <span className="font-medium">Puja ganadora actual</span>
                </div>
              )}

              {isUserBid && !isWinning && (
                <div className="mt-2 text-sm text-blue-600">
                  Tu puja ha sido superada
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Load More Button */}
      {hasMoreBids && (
        <div className="mt-4 text-center">
          <button
            onClick={() => setShowAll(!showAll)}
            className="px-4 py-2 text-sm text-primary-600 hover:text-primary-700 hover:bg-primary-50 rounded-lg transition-colors"
          >
            {showAll ? 'Mostrar menos' : `Ver todas las pujas (${bids.length})`}
          </button>
        </div>
      )}

      {/* Statistics */}
      <div className="mt-6 pt-4 border-t border-gray-200">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center text-sm">
          <div>
            <div className="font-semibold text-gray-900">{bids.length}</div>
            <div className="text-gray-600">Total pujas</div>
          </div>
          
          <div>
            <div className="font-semibold text-gray-900">
              {new Set(bids.map(bid => bid.username)).size}
            </div>
            <div className="text-gray-600">Participantes</div>
          </div>
          
          <div>
            <div className="font-semibold text-gray-900">
              {bids.length > 0 ? formatPrice(Math.min(...bids.map(bid => bid.bidPrice))) : '-'}
            </div>
            <div className="text-gray-600">Puja más baja</div>
          </div>
          
          <div>
            <div className="font-semibold text-gray-900">
              {bids.length > 0 ? formatPrice(Math.max(...bids.map(bid => bid.bidPrice))) : '-'}
            </div>
            <div className="text-gray-600">Puja más alta</div>
          </div>
        </div>
      </div>

      {/* User's bid summary */}
      {user && bids.some(bid => bid.username === user.username) && (
        <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
          <h4 className="font-medium text-blue-900 mb-2">Tus pujas</h4>
          <div className="space-y-1 text-sm">
            {bids
              .filter(bid => bid.username === user.username)
              .slice(0, 3)
              .map(bid => (
                <div key={bid.bidId} className="flex justify-between">
                  <span className="text-blue-700">{formatPrice(bid.bidPrice)}</span>
                  <span className="text-blue-600">
                    {moment().format('DD/MM HH:mm')}
                  </span>
                </div>
              ))}
          </div>
          {bids.filter(bid => bid.username === user.username).length > 3 && (
            <div className="text-xs text-blue-600 mt-1">
              Y {bids.filter(bid => bid.username === user.username).length - 3} más...
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default BidHistory;