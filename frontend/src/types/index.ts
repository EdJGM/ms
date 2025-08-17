export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  role: 'PARTICIPANTE' | 'MODERADOR' | 'ADMINISTRADOR';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  email: string;
  username: string;
  role: string;
}

export interface Auction {
  auctionId: number;
  description: string;
  startingPrice: number;
  itemStatus: string;
  itemCategory: string;
  daysToEndTime: number;
  ownerUsername: string;
  estado: 'programada' | 'activa' | 'finalizada';
  precioActual: number;
  incrementoMinimo: number;
  fechaInicio: string;
  fechaFin: string;
}

export interface AuctionRequest {
  description: string;
  startingPrice: number;
  itemStatus: string;
  itemCategory: string;
  daysToEndTime: number;
  incrementoMinimo?: number;
}

export interface AuctionView extends Auction {
  highestBid?: number;
  bidCount?: number;
  timeRemaining?: string;
}

export interface Bid {
  bidId: number;
  auctionId: number;
  userId: number;
  username: string;
  bidPrice: number;
}

export interface BidRequest {
  amount: number;
}

export interface Notification {
  id: number;
  userId?: number;
  title: string;
  message: string;
  type: string;
  email?: string;
  subject?: string;
  text?: string;
  sentBy: string;
  sentAt: string;
}

export interface NotificationRequest {
  userId?: number;
  title: string;
  message: string;
  type: string;
  email?: string;
  subject?: string;
  text?: string;
}

export interface Product {
  id: number;
  nombre: string;
  descripcion: string;
  categoria: string;
  estado: string;
  precio: number;
  vendedor: string;
}

export interface UserHistory {
  auctions: Auction[];
  bids: Bid[];
  notifications: Notification[];
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

export interface PaginatedResponse<T> {
  items: T[];
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

export interface WebSocketMessage {
  type: 'NEW_BID' | 'AUCTION_EXTENDED' | 'AUCTION_ENDED' | 'MODERATOR_JOINED' | 'NOTIFICATION';
  data: any;
  timestamp: string;
}