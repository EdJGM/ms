import { create } from 'zustand';
import { AuctionView, Bid } from '@/types';
import { api } from '@/lib/api';

interface AuctionState {
  auctions: AuctionView[];
  currentAuction: AuctionView | null;
  currentBids: Bid[];
  isLoading: boolean;
  error: string | null;
  filters: {
    categoria?: string;
    searchTerm?: string;
    page: number;
    limit: number;
  };

  // Actions
  loadAuctions: () => Promise<void>;
  loadAuction: (id: number) => Promise<void>;
  loadBids: (auctionId: number) => Promise<void>;
  searchAuctions: (searchTerm?: string, categoria?: string) => Promise<void>;
  createAuction: (auctionData: any) => Promise<void>;
  updateAuction: (id: number, auctionData: any) => Promise<void>;
  deleteAuction: (id: number) => Promise<void>;
  placeBid: (auctionId: number, amount: number) => Promise<void>;
  setFilters: (filters: Partial<AuctionState['filters']>) => void;
  clearError: () => void;
  setCurrentAuction: (auction: AuctionView | null) => void;
  updateAuctionInList: (updatedAuction: AuctionView) => void;
  addNewBid: (bid: Bid) => void;
}

export const useAuctionStore = create<AuctionState>((set, get) => ({
  auctions: [],
  currentAuction: null,
  currentBids: [],
  isLoading: false,
  error: null,
  filters: {
    page: 0,
    limit: 10
  },

  loadAuctions: async () => {
    try {
      set({ isLoading: true, error: null });
      const { categoria, page, limit } = get().filters;
      const auctions = await api.getAuctions(categoria, page, limit);
      set({ auctions, isLoading: false });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al cargar subastas';
      set({ error: errorMessage, isLoading: false });
    }
  },

  loadAuction: async (id: number) => {
    try {
      set({ isLoading: true, error: null });
      const auction = await api.getAuction(id);
      set({ currentAuction: auction, isLoading: false });
      
      // Also load bids for this auction
      get().loadBids(id);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al cargar subasta';
      set({ error: errorMessage, isLoading: false });
    }
  },

  loadBids: async (auctionId: number) => {
    try {
      const bids = await api.getBidsForAuction(auctionId);
      set({ currentBids: bids.sort((a, b) => b.bidPrice - a.bidPrice) });
    } catch (error: any) {
      console.error('Error loading bids:', error);
    }
  },

  searchAuctions: async (searchTerm?: string, categoria?: string) => {
    try {
      set({ isLoading: true, error: null });
      const { page, limit } = get().filters;
      const auctions = await api.searchAuctions(searchTerm, categoria, page, limit);
      set({ 
        auctions, 
        isLoading: false,
        filters: { ...get().filters, searchTerm, categoria }
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error en la búsqueda';
      set({ error: errorMessage, isLoading: false });
    }
  },

  createAuction: async (auctionData) => {
    try {
      set({ isLoading: true, error: null });
      const newAuction = await api.createAuction(auctionData);
      set({ 
        auctions: [newAuction, ...get().auctions],
        isLoading: false 
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al crear subasta';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  updateAuction: async (id: number, auctionData) => {
    try {
      set({ isLoading: true, error: null });
      const updatedAuction = await api.updateAuction(id, auctionData);
      
      set({ 
        auctions: get().auctions.map(a => a.auctionId === id ? updatedAuction : a),
        currentAuction: get().currentAuction?.auctionId === id ? updatedAuction : get().currentAuction,
        isLoading: false 
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al actualizar subasta';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  deleteAuction: async (id: number) => {
    try {
      set({ isLoading: true, error: null });
      await api.deleteAuction(id);
      
      set({ 
        auctions: get().auctions.filter(a => a.auctionId !== id),
        currentAuction: get().currentAuction?.auctionId === id ? null : get().currentAuction,
        isLoading: false 
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al eliminar subasta';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  placeBid: async (auctionId: number, amount: number) => {
    try {
      set({ isLoading: true, error: null });
      const response = await api.createBid(auctionId, { amount });
      
      if (response.success && response.data) {
        // Add the new bid to the current bids list
        set({ 
          currentBids: [response.data, ...get().currentBids].sort((a, b) => b.bidPrice - a.bidPrice),
          isLoading: false 
        });
        
        // Update auction's current price
        if (get().currentAuction?.auctionId === auctionId) {
          set({
            currentAuction: {
              ...get().currentAuction!,
              precioActual: amount
            }
          });
        }
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error al realizar puja';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  setFilters: (newFilters) => {
    set({ 
      filters: { ...get().filters, ...newFilters }
    });
  },

  clearError: () => {
    set({ error: null });
  },

  setCurrentAuction: (auction) => {
    set({ currentAuction: auction });
  },

  updateAuctionInList: (updatedAuction) => {
    set({
      auctions: get().auctions.map(a => 
        a.auctionId === updatedAuction.auctionId ? updatedAuction : a
      )
    });
  },

  addNewBid: (bid) => {
    set({
      currentBids: [bid, ...get().currentBids].sort((a, b) => b.bidPrice - a.bidPrice)
    });
    
    // Update current auction price if it matches
    if (get().currentAuction?.auctionId === bid.auctionId) {
      set({
        currentAuction: {
          ...get().currentAuction!,
          precioActual: bid.bidPrice
        }
      });
    }
  }
}));