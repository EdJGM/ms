'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import { useAuctionStore } from '@/store/auctionStore';
import BiddingInterface from '@/components/auctions/BiddingInterface';
import BidHistory from '@/components/auctions/BidHistory';
import { websocketClient } from '@/lib/websocket';
import { 
  ClockIcon, 
  UserIcon, 
  TagIcon,
  CalendarIcon,
  CurrencyDollarIcon,
  ExclamationTriangleIcon,
  ArrowLeftIcon
} from '@heroicons/react/24/outline';
import moment from 'moment';
import toast from 'react-hot-toast';

export default function AuctionDetailPage() {
  const params = useParams();
  const router = useRouter();
  const auctionId = params?.id ? parseInt(params.id as string) : null;
  
  const { isAuthenticated, user } = useAuthStore();
  const { currentAuction, currentBids, loadAuction, loadBids, isLoading, error } = useAuctionStore();
  
  const [timeRemaining, setTimeRemaining] = useState<string>('');
  const [isWsConnected, setIsWsConnected] = useState(false);

  useEffect(() => {
    if (!auctionId) {
      router.push('/auctions');
      return;
    }

    loadAuction(auctionId);
  }, [auctionId, loadAuction, router]);

  // WebSocket connection for real-time updates
  useEffect(() => {
    if (!auctionId || !isAuthenticated) return;

    const connectWebSocket = async () => {
      try {
        await websocketClient.connect();
        websocketClient.joinAuction(auctionId);
        setIsWsConnected(true);

        // Listen for new bids
        websocketClient.onNewBid((data) => {
          toast.success('¡Nueva puja realizada!');
          loadBids(auctionId);
          if (currentAuction) {
            loadAuction(auctionId);
          }
        });

        // Listen for auction extensions
        websocketClient.onAuctionExtended((data) => {
          toast.info('La subasta ha sido extendida');
          loadAuction(auctionId);
        });

        // Listen for auction end
        websocketClient.onAuctionEnded((data) => {
          toast.info('La subasta ha finalizado');
          loadAuction(auctionId);
        });

        // Listen for moderator actions
        websocketClient.onModeratorJoined((data) => {
          toast.info('Un moderador se ha unido a la subasta');
        });

      } catch (error) {
        console.error('WebSocket connection failed:', error);
        setIsWsConnected(false);
      }
    };

    connectWebSocket();

    return () => {
      if (auctionId) {
        websocketClient.leaveAuction(auctionId);
      }
      websocketClient.disconnect();
      setIsWsConnected(false);
    };
  }, [auctionId, isAuthenticated, currentAuction, loadAuction, loadBids]);

  // Update time remaining
  useEffect(() => {
    if (!currentAuction?.fechaFin) return;

    const updateTimer = () => {
      const endTime = moment(currentAuction.fechaFin);
      const now = moment();
      
      if (endTime.isBefore(now)) {
        setTimeRemaining('Finalizada');
        return;
      }
      
      const duration = moment.duration(endTime.diff(now));
      const days = Math.floor(duration.asDays());
      const hours = duration.hours();
      const minutes = duration.minutes();
      const seconds = duration.seconds();
      
      if (days > 0) {
        setTimeRemaining(`${days}d ${hours}h ${minutes}m ${seconds}s`);
      } else if (hours > 0) {
        setTimeRemaining(`${hours}h ${minutes}m ${seconds}s`);
      } else if (minutes > 0) {
        setTimeRemaining(`${minutes}m ${seconds}s`);
      } else {
        setTimeRemaining(`${seconds}s`);
      }
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [currentAuction?.fechaFin]);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const getStatusInfo = () => {
    if (!currentAuction) return null;
    
    switch (currentAuction.estado) {
      case 'activa':
        return {
          color: 'bg-green-100 text-green-800',
          label: 'Activa',
          icon: '🟢'
        };
      case 'programada':
        return {
          color: 'bg-yellow-100 text-yellow-800',
          label: 'Programada',
          icon: '🟡'
        };
      case 'finalizada':
        return {
          color: 'bg-red-100 text-red-800',
          label: 'Finalizada',
          icon: '🔴'
        };
      default:
        return {
          color: 'bg-gray-100 text-gray-800',
          label: currentAuction.estado,
          icon: '⚪'
        };
    }
  };

  const statusInfo = getStatusInfo();
  const currentPrice = currentAuction?.precioActual || currentAuction?.startingPrice || 0;
  const highestBid = currentBids.length > 0 ? currentBids[0] : null;
  const userIsOwner = user?.username === currentAuction?.ownerUsername;
  const userHasBid = currentBids.some(bid => bid.username === user?.username);

  if (isLoading && !currentAuction) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="spinner mx-auto mb-4"></div>
          <p className="text-gray-600">Cargando subasta...</p>
        </div>
      </div>
    );
  }

  if (error || !currentAuction) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <ExclamationTriangleIcon className="mx-auto h-12 w-12 text-red-500 mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Subasta no encontrada
          </h2>
          <p className="text-gray-600 mb-4">
            {error || 'La subasta que buscas no existe o ha sido eliminada.'}
          </p>
          <button
            onClick={() => router.push('/auctions')}
            className="btn-primary inline-flex items-center"
          >
            <ArrowLeftIcon className="h-4 w-4 mr-2" />
            Volver a Subastas
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <button
            onClick={() => router.back()}
            className="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
          >
            <ArrowLeftIcon className="h-5 w-5 mr-2" />
            Volver
          </button>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Auction Header */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h1 className="text-2xl font-bold text-gray-900 mb-2">
                    {currentAuction.description}
                  </h1>
                  <div className="flex items-center space-x-4 text-sm text-gray-600">
                    <div className="flex items-center">
                      <UserIcon className="h-4 w-4 mr-1" />
                      <span>Vendedor: {currentAuction.ownerUsername}</span>
                    </div>
                    {currentAuction.itemCategory && (
                      <div className="flex items-center">
                        <TagIcon className="h-4 w-4 mr-1" />
                        <span>{currentAuction.itemCategory}</span>
                      </div>
                    )}
                  </div>
                </div>
                
                {statusInfo && (
                  <span className={`px-3 py-1 rounded-full text-sm font-medium ${statusInfo.color}`}>
                    {statusInfo.icon} {statusInfo.label}
                  </span>
                )}
              </div>

              {/* Connection Status */}
              {isAuthenticated && (
                <div className="mb-4 p-3 rounded-lg bg-gray-50">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-gray-600">Estado de conexión:</span>
                    <span className={`flex items-center ${isWsConnected ? 'text-green-600' : 'text-red-600'}`}>
                      <div className={`w-2 h-2 rounded-full mr-2 ${isWsConnected ? 'bg-green-500' : 'bg-red-500'}`}></div>
                      {isWsConnected ? 'Conectado en tiempo real' : 'Desconectado'}
                    </span>
                  </div>
                </div>
              )}

              {/* Time Information */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                <div className="flex items-center text-sm text-gray-600">
                  <CalendarIcon className="h-4 w-4 mr-2" />
                  <div>
                    <div>Inicio: {moment(currentAuction.fechaInicio).format('DD/MM/YYYY HH:mm')}</div>
                    <div>Fin: {moment(currentAuction.fechaFin).format('DD/MM/YYYY HH:mm')}</div>
                  </div>
                </div>
                
                {currentAuction.estado === 'activa' && timeRemaining !== 'Finalizada' && (
                  <div className="flex items-center">
                    <ClockIcon className="h-4 w-4 mr-2 text-primary-600" />
                    <div>
                      <div className="text-sm text-gray-600">Tiempo restante:</div>
                      <div className="text-lg font-bold text-primary-600">{timeRemaining}</div>
                    </div>
                  </div>
                )}
              </div>

              {/* Pricing */}
              <div className="bg-gray-50 rounded-lg p-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <div className="text-sm text-gray-600 mb-1">Precio inicial</div>
                    <div className="text-xl font-semibold">
                      {formatPrice(currentAuction.startingPrice)}
                    </div>
                  </div>
                  
                  <div>
                    <div className="text-sm text-gray-600 mb-1">
                      {currentBids.length > 0 ? 'Puja más alta' : 'Precio actual'}
                    </div>
                    <div className="text-2xl font-bold text-auction-active flex items-center">
                      <CurrencyDollarIcon className="h-6 w-6 mr-1" />
                      {formatPrice(currentPrice)}
                    </div>
                    {currentBids.length > 0 && (
                      <div className="text-sm text-gray-600">
                        por {highestBid?.username}
                      </div>
                    )}
                  </div>
                </div>
              </div>

              {/* Auction Stats */}
              <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
                <div>
                  <div className="text-lg font-bold text-gray-900">{currentBids.length}</div>
                  <div className="text-sm text-gray-600">Pujas</div>
                </div>
                <div>
                  <div className="text-lg font-bold text-gray-900">
                    {new Set(currentBids.map(bid => bid.username)).size}
                  </div>
                  <div className="text-sm text-gray-600">Participantes</div>
                </div>
                <div>
                  <div className="text-lg font-bold text-gray-900">{currentAuction.daysToEndTime}</div>
                  <div className="text-sm text-gray-600">Días programados</div>
                </div>
                <div>
                  <div className="text-lg font-bold text-gray-900">
                    {currentAuction.incrementoMinimo ? formatPrice(currentAuction.incrementoMinimo) : 'N/A'}
                  </div>
                  <div className="text-sm text-gray-600">Incremento mín.</div>
                </div>
              </div>
            </div>

            {/* Bid History */}
            <BidHistory bids={currentBids} />
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Bidding Interface */}
            {isAuthenticated && !userIsOwner && currentAuction.estado === 'activa' ? (
              <BiddingInterface
                auction={currentAuction}
                currentPrice={currentPrice}
                minimumIncrement={currentAuction.incrementoMinimo || 1}
                userHasBid={userHasBid}
              />
            ) : isAuthenticated && userIsOwner ? (
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h3 className="font-medium text-blue-900 mb-2">Tu subasta</h3>
                <p className="text-sm text-blue-700">
                  Eres el propietario de esta subasta. No puedes pujar en tus propias subastas.
                </p>
              </div>
            ) : !isAuthenticated ? (
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <h3 className="font-medium text-yellow-900 mb-2">Inicia sesión para pujar</h3>
                <p className="text-sm text-yellow-700 mb-3">
                  Necesitas iniciar sesión para participar en esta subasta.
                </p>
                <button
                  onClick={() => router.push('/auth/login?redirect=' + encodeURIComponent(window.location.pathname))}
                  className="btn-primary w-full text-sm"
                >
                  Iniciar Sesión
                </button>
              </div>
            ) : (
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <h3 className="font-medium text-gray-900 mb-2">Subasta no disponible</h3>
                <p className="text-sm text-gray-600">
                  Esta subasta no está activa para recibir pujas.
                </p>
              </div>
            )}

            {/* Auction Information */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Información adicional
              </h3>
              
              <div className="space-y-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Estado del artículo:</span>
                  <span className="font-medium">{currentAuction.itemStatus || 'No especificado'}</span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-gray-600">Incremento mínimo:</span>
                  <span className="font-medium">
                    {currentAuction.incrementoMinimo ? formatPrice(currentAuction.incrementoMinimo) : 'Libre'}
                  </span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-gray-600">Duración:</span>
                  <span className="font-medium">{currentAuction.daysToEndTime} días</span>
                </div>

                {userHasBid && (
                  <div className="mt-4 p-3 bg-green-50 rounded-lg">
                    <div className="flex items-center text-green-800 text-sm">
                      <div className="w-2 h-2 bg-green-500 rounded-full mr-2"></div>
                      Has participado en esta subasta
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Safety Tips */}
            <div className="bg-white rounded-lg shadow-sm border p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Consejos de seguridad
              </h3>
              <ul className="space-y-2 text-sm text-gray-600">
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Revisa la reputación del vendedor
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Lee toda la información del producto
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Establece un límite de gasto
                </li>
                <li className="flex items-start">
                  <span className="text-green-500 mr-2">✓</span>
                  Usa métodos de pago seguros
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}