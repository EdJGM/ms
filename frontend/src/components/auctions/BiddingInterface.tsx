'use client';

import React, { useState, useEffect } from 'react';
import { AuctionView } from '@/types';
import { useAuctionStore } from '@/store/auctionStore';
import { useAuthStore } from '@/store/authStore';
import { 
  CurrencyDollarIcon, 
  ExclamationTriangleIcon,
  CheckCircleIcon,
  ClockIcon
} from '@heroicons/react/24/outline';
import toast from 'react-hot-toast';

interface BiddingInterfaceProps {
  auction: AuctionView;
  currentPrice: number;
  minimumIncrement: number;
  userHasBid: boolean;
}

const BiddingInterface: React.FC<BiddingInterfaceProps> = ({
  auction,
  currentPrice,
  minimumIncrement,
  userHasBid
}) => {
  const { placeBid, isLoading } = useAuctionStore();
  const { user } = useAuthStore();
  
  const [bidAmount, setBidAmount] = useState<string>('');
  const [quickBidAmount, setQuickBidAmount] = useState<number>(0);
  const [isValidating, setIsValidating] = useState(false);
  const [validationError, setValidationError] = useState<string>('');
  const [confirmBid, setConfirmBid] = useState(false);

  const minimumBid = currentPrice + minimumIncrement;

  useEffect(() => {
    setQuickBidAmount(minimumBid);
    setBidAmount(minimumBid.toString());
  }, [minimumBid]);

  const validateBidAmount = (amount: number): string | null => {
    if (amount <= currentPrice) {
      return `La puja debe ser mayor a ${formatPrice(currentPrice)}`;
    }
    
    if (amount < minimumBid) {
      return `La puja mínima es ${formatPrice(minimumBid)}`;
    }
    
    if (amount > 999999) {
      return 'El monto máximo permitido es €999,999';
    }
    
    return null;
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const handleBidAmountChange = (value: string) => {
    setBidAmount(value);
    setValidationError('');
    
    const numericValue = parseFloat(value);
    if (!isNaN(numericValue)) {
      const error = validateBidAmount(numericValue);
      if (error) {
        setValidationError(error);
      }
    }
  };

  const handleQuickBid = (increment: number) => {
    const newAmount = minimumBid + increment;
    setBidAmount(newAmount.toString());
    setQuickBidAmount(newAmount);
    setValidationError('');
  };

  const handleSubmitBid = async () => {
    const numericAmount = parseFloat(bidAmount);
    
    if (isNaN(numericAmount)) {
      setValidationError('Ingresa un monto válido');
      return;
    }
    
    const validationError = validateBidAmount(numericAmount);
    if (validationError) {
      setValidationError(validationError);
      return;
    }

    if (!confirmBid) {
      setConfirmBid(true);
      return;
    }

    try {
      setIsValidating(true);
      await placeBid(auction.auctionId, numericAmount);
      
      toast.success('¡Puja realizada exitosamente!');
      setBidAmount(minimumBid.toString());
      setConfirmBid(false);
      setValidationError('');
      
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || 'Error al realizar la puja';
      setValidationError(errorMessage);
      toast.error(errorMessage);
      setConfirmBid(false);
    } finally {
      setIsValidating(false);
    }
  };

  const handleCancelConfirm = () => {
    setConfirmBid(false);
  };

  const quickBidOptions = [
    { label: '+€5', increment: 5 },
    { label: '+€10', increment: 10 },
    { label: '+€25', increment: 25 },
    { label: '+€50', increment: 50 }
  ];

  const isActive = auction.estado === 'activa';
  const hasError = validationError !== '';
  const canSubmit = !hasError && bidAmount && !isLoading && !isValidating && isActive;

  if (!isActive) {
    return (
      <div className="bg-gray-50 border border-gray-200 rounded-lg p-6">
        <div className="text-center">
          <ClockIcon className="mx-auto h-8 w-8 text-gray-400 mb-3" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            Subasta no activa
          </h3>
          <p className="text-sm text-gray-600">
            Esta subasta no está disponible para recibir pujas.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">
        Realizar Puja
      </h3>

      {/* Current Status */}
      <div className="mb-4 p-3 bg-primary-50 rounded-lg">
        <div className="flex items-center justify-between text-sm">
          <span className="text-primary-700">Precio actual:</span>
          <span className="font-bold text-primary-900">{formatPrice(currentPrice)}</span>
        </div>
        <div className="flex items-center justify-between text-sm mt-1">
          <span className="text-primary-700">Puja mínima:</span>
          <span className="font-bold text-primary-900">{formatPrice(minimumBid)}</span>
        </div>
      </div>

      {/* User Status */}
      {userHasBid && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
          <div className="flex items-center text-green-800 text-sm">
            <CheckCircleIcon className="h-4 w-4 mr-2" />
            Ya has participado en esta subasta
          </div>
        </div>
      )}

      {!confirmBid ? (
        <>
          {/* Quick Bid Options */}
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Pujas rápidas
            </label>
            <div className="grid grid-cols-2 gap-2">
              {quickBidOptions.map((option) => (
                <button
                  key={option.increment}
                  onClick={() => handleQuickBid(option.increment)}
                  className="px-3 py-2 text-sm border border-gray-300 rounded-md hover:border-primary-500 hover:text-primary-600 transition-colors"
                  disabled={isLoading}
                >
                  {option.label}
                </button>
              ))}
            </div>
          </div>

          {/* Custom Bid Amount */}
          <div className="mb-4">
            <label htmlFor="bidAmount" className="block text-sm font-medium text-gray-700 mb-2">
              Monto personalizado
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <CurrencyDollarIcon className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="bidAmount"
                type="number"
                step="0.01"
                min={minimumBid}
                value={bidAmount}
                onChange={(e) => handleBidAmountChange(e.target.value)}
                className={`w-full pl-10 pr-3 py-2 border rounded-md focus:ring-2 focus:ring-primary-500 focus:border-primary-500 ${
                  hasError ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder={minimumBid.toString()}
                disabled={isLoading}
              />
            </div>
            {hasError && (
              <p className="mt-1 text-sm text-red-600 flex items-center">
                <ExclamationTriangleIcon className="h-4 w-4 mr-1" />
                {validationError}
              </p>
            )}
          </div>

          {/* Submit Button */}
          <button
            onClick={handleSubmitBid}
            disabled={!canSubmit}
            className="w-full bg-primary-600 text-white py-3 px-4 rounded-md font-medium hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isLoading || isValidating ? (
              <div className="flex items-center justify-center">
                <div className="spinner mr-2"></div>
                Procesando...
              </div>
            ) : (
              `Pujar ${bidAmount ? formatPrice(parseFloat(bidAmount)) : ''}`
            )}
          </button>
        </>
      ) : (
        /* Confirmation Dialog */
        <div className="space-y-4">
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
            <div className="flex items-start">
              <ExclamationTriangleIcon className="h-5 w-5 text-yellow-600 mt-0.5 mr-3" />
              <div>
                <h4 className="text-sm font-medium text-yellow-800 mb-1">
                  Confirmar Puja
                </h4>
                <p className="text-sm text-yellow-700">
                  Estás a punto de pujar <strong>{formatPrice(parseFloat(bidAmount))}</strong> 
                  en esta subasta. Esta acción no se puede deshacer.
                </p>
              </div>
            </div>
          </div>

          <div className="flex space-x-3">
            <button
              onClick={handleCancelConfirm}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors"
              disabled={isValidating}
            >
              Cancelar
            </button>
            <button
              onClick={handleSubmitBid}
              className="flex-1 bg-primary-600 text-white py-2 px-4 rounded-md font-medium hover:bg-primary-700 transition-colors"
              disabled={isValidating}
            >
              {isValidating ? (
                <div className="flex items-center justify-center">
                  <div className="spinner mr-2"></div>
                  Confirmando...
                </div>
              ) : (
                'Confirmar Puja'
              )}
            </button>
          </div>
        </div>
      )}

      {/* Disclaimer */}
      <div className="mt-4 text-xs text-gray-500">
        <p>
          Al realizar una puja, te comprometes a comprar el artículo si resultas ganador. 
          Las pujas son vinculantes y no pueden ser canceladas.
        </p>
      </div>
    </div>
  );
};

export default BiddingInterface;