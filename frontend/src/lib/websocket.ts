import { io, Socket } from 'socket.io-client';
import { getAuthToken } from './auth';
import { WebSocketMessage } from '@/types';

export class WebSocketClient {
  private socket: Socket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000;

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      const token = getAuthToken();
      if (!token) {
        reject(new Error('No auth token available'));
        return;
      }

      this.socket = io(process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8085', {
        auth: {
          token: `Bearer ${token}`
        },
        transports: ['websocket'],
        upgrade: false
      });

      this.socket.on('connect', () => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        resolve();
      });

      this.socket.on('disconnect', (reason) => {
        console.log('WebSocket disconnected:', reason);
        if (reason === 'io server disconnect') {
          // Server disconnected, try to reconnect
          this.attemptReconnect();
        }
      });

      this.socket.on('connect_error', (error) => {
        console.error('WebSocket connection error:', error);
        reject(error);
      });

      this.socket.on('error', (error) => {
        console.error('WebSocket error:', error);
      });
    });
  }

  private attemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
    
    setTimeout(() => {
      console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      this.connect().catch(console.error);
    }, delay);
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }

  // Subscribe to auction-specific events
  joinAuction(auctionId: number) {
    if (this.socket) {
      this.socket.emit('join-auction', auctionId);
    }
  }

  leaveAuction(auctionId: number) {
    if (this.socket) {
      this.socket.emit('leave-auction', auctionId);
    }
  }

  // Event listeners
  onNewBid(callback: (data: any) => void) {
    if (this.socket) {
      this.socket.on('new-bid', callback);
    }
  }

  onAuctionExtended(callback: (data: any) => void) {
    if (this.socket) {
      this.socket.on('auction-extended', callback);
    }
  }

  onAuctionEnded(callback: (data: any) => void) {
    if (this.socket) {
      this.socket.on('auction-ended', callback);
    }
  }

  onModeratorJoined(callback: (data: any) => void) {
    if (this.socket) {
      this.socket.on('moderator-joined', callback);
    }
  }

  onNotification(callback: (data: any) => void) {
    if (this.socket) {
      this.socket.on('notification', callback);
    }
  }

  onMessage(callback: (message: WebSocketMessage) => void) {
    if (this.socket) {
      this.socket.on('message', callback);
    }
  }

  // Remove event listeners
  off(event: string, callback?: Function) {
    if (this.socket) {
      this.socket.off(event, callback);
    }
  }

  // Send custom messages
  emit(event: string, data: any) {
    if (this.socket) {
      this.socket.emit(event, data);
    }
  }

  isConnected(): boolean {
    return this.socket?.connected || false;
  }
}

// Singleton instance
export const websocketClient = new WebSocketClient();