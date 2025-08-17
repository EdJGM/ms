import axios, { AxiosInstance, AxiosResponse } from 'axios';
import Cookies from 'js-cookie';
import { 
  AuthResponse, 
  LoginRequest, 
  RegisterRequest, 
  User, 
  Auction, 
  AuctionRequest,
  AuctionView,
  Bid,
  BidRequest,
  Notification,
  NotificationRequest,
  Product,
  UserHistory,
  ApiResponse
} from '@/types';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8000',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = Cookies.get('auction_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle common errors
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token expired or invalid
          Cookies.remove('auction_token');
          Cookies.remove('auction_user');
          window.location.href = '/auth/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await this.client.post('/api/v1/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<string>> {
    const response = await this.client.post('/api/v1/auth/register', userData);
    return response.data;
  }

  async refreshToken(): Promise<AuthResponse> {
    const response = await this.client.post('/api/v1/auth/refreshToken');
    return response.data;
  }

  async logout(): Promise<void> {
    await this.client.post('/api/v1/auth/logout');
  }

  async validateToken(): Promise<boolean> {
    try {
      await this.client.get('/api/v1/auth/validateToken');
      return true;
    } catch {
      return false;
    }
  }

  // User endpoints
  async getUsers(): Promise<User[]> {
    const response = await this.client.get('/api/v1/admin/users');
    return response.data;
  }

  async getUserByEmail(email: string): Promise<User> {
    const response = await this.client.get(`/api/v1/admin/users/by-email?email=${email}`);
    return response.data;
  }

  async deleteUser(userId: number): Promise<void> {
    await this.client.delete(`/api/v1/admin/users/${userId}`);
  }

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await this.client.put('/api/v1/users/me/password', {
      currentPassword,
      newPassword
    });
  }

  // Auction endpoints
  async getAuctions(categoria?: string, page = 0, limit = 10): Promise<AuctionView[]> {
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });
    if (categoria) params.append('categoria', categoria);

    const response = await this.client.get(`/api/v1/subastas?${params}`);
    return response.data;
  }

  async getAuction(id: number): Promise<AuctionView> {
    const response = await this.client.get(`/api/v1/subastas/${id}`);
    return response.data;
  }

  async searchAuctions(searchTerm?: string, categoria?: string, page = 0, limit = 10): Promise<AuctionView[]> {
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });
    if (searchTerm) params.append('searchTerm', searchTerm);
    if (categoria) params.append('categoria', categoria);

    const response = await this.client.get(`/api/v1/subastas/search?${params}`);
    return response.data;
  }

  async createAuction(auction: AuctionRequest): Promise<Auction> {
    const response = await this.client.post('/api/v1/subastas', auction);
    return response.data;
  }

  async updateAuction(id: number, auction: AuctionRequest): Promise<Auction> {
    const response = await this.client.put(`/api/v1/subastas/${id}`, auction);
    return response.data;
  }

  async deleteAuction(id: number): Promise<void> {
    await this.client.delete(`/api/v1/subastas/${id}`);
  }

  async startAuction(id: number): Promise<Auction> {
    const response = await this.client.post(`/api/v1/subastas/${id}/start`);
    return response.data;
  }

  async endAuction(id: number): Promise<Auction> {
    const response = await this.client.post(`/api/v1/subastas/${id}/end`);
    return response.data;
  }

  async extendAuction(id: number, minutes: number): Promise<Auction> {
    const response = await this.client.post(`/api/v1/subastas/${id}/extend`, { minutes });
    return response.data;
  }

  async joinModerationSession(id: number): Promise<void> {
    await this.client.post(`/api/v1/subastas/${id}/moderationsession`);
  }

  async leaveModerationSession(id: number): Promise<void> {
    await this.client.delete(`/api/v1/subastas/${id}/moderationsession`);
  }

  async isAuctionActive(id: number): Promise<boolean> {
    const response = await this.client.get(`/api/v1/subastas/${id}/status`);
    return response.data;
  }

  async auctionExists(id: number): Promise<boolean> {
    const response = await this.client.get(`/api/v1/subastas/${id}/exists`);
    return response.data;
  }

  // Bid endpoints
  async createBid(auctionId: number, bid: BidRequest): Promise<ApiResponse<Bid>> {
    const response = await this.client.post(`/api/v1/subastas/${auctionId}/pujas`, bid);
    return response.data;
  }

  async validateBid(auctionId: number, bid: BidRequest): Promise<ApiResponse<boolean>> {
    const response = await this.client.post(`/api/v1/subastas/${auctionId}/pujas/validate`, bid);
    return response.data;
  }

  async getBidsForAuction(auctionId: number): Promise<Bid[]> {
    const response = await this.client.get(`/api/v1/subastas/${auctionId}/pujas`);
    return response.data;
  }

  async getBid(auctionId: number, bidId: number): Promise<Bid> {
    const response = await this.client.get(`/api/v1/subastas/${auctionId}/pujas/${bidId}`);
    return response.data;
  }

  async getHighestBid(auctionId: number): Promise<Bid> {
    const response = await this.client.get(`/api/v1/subastas/${auctionId}/pujas/highest`);
    return response.data;
  }

  async deleteBid(auctionId: number, bidId: number, userId: number): Promise<void> {
    await this.client.delete(`/api/v1/subastas/${auctionId}/pujas/${bidId}?userId=${userId}`);
  }

  // Product endpoints
  async getProducts(): Promise<Product[]> {
    const response = await this.client.get('/api/v1/productos');
    return response.data;
  }

  async getProduct(id: number): Promise<Product> {
    const response = await this.client.get(`/api/v1/productos/${id}`);
    return response.data;
  }

  async createProduct(product: Omit<Product, 'id'>): Promise<Product> {
    const response = await this.client.post('/api/v1/productos', product);
    return response.data;
  }

  async updateProduct(id: number, product: Partial<Product>): Promise<Product> {
    const response = await this.client.put(`/api/v1/productos/${id}`, product);
    return response.data;
  }

  async deleteProduct(id: number): Promise<void> {
    await this.client.delete(`/api/v1/productos/${id}`);
  }

  // Notification endpoints
  async sendNotification(notification: NotificationRequest): Promise<Notification> {
    const response = await this.client.post('/api/v1/notifications/send', notification);
    return response.data;
  }

  async getNotificationHistory(): Promise<Notification[]> {
    const response = await this.client.get('/api/v1/notifications/history');
    return response.data;
  }

  async getUserNotifications(userId: number): Promise<Notification[]> {
    const response = await this.client.get(`/api/v1/notifications/user/${userId}`);
    return response.data;
  }

  // User history
  async getUserHistory(): Promise<UserHistory> {
    const response = await this.client.get('/api/v1/usuarios/me/historial');
    return response.data;
  }
}

export const api = new ApiClient();
export default api;