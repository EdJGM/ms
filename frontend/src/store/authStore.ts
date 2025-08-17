import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { User, AuthResponse } from '@/types';
import { api } from '@/lib/api';
import { setAuthData, clearAuthData, getUserData, getAuthToken } from '@/lib/auth';

interface AuthState {
  user: Partial<User> | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  login: (email: string, password: string) => Promise<void>;
  register: (userData: {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
  }) => Promise<void>;
  logout: () => void;
  validateAuth: () => Promise<boolean>;
  clearError: () => void;
  setLoading: (loading: boolean) => void;
  initialize: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      login: async (email: string, password: string) => {
        try {
          set({ isLoading: true, error: null });
          
          const authResponse: AuthResponse = await api.login({ email, password });
          setAuthData(authResponse);
          
          set({
            user: {
              email: authResponse.email,
              username: authResponse.username,
              role: authResponse.role as any
            },
            isAuthenticated: true,
            isLoading: false,
            error: null
          });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Error al iniciar sesión';
          set({ 
            isLoading: false, 
            error: errorMessage,
            isAuthenticated: false,
            user: null 
          });
          throw new Error(errorMessage);
        }
      },

      register: async (userData) => {
        try {
          set({ isLoading: true, error: null });
          
          await api.register(userData);
          
          // After successful registration, automatically log in
          await get().login(userData.email, userData.password);
          
          set({ isLoading: false, error: null });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Error al registrar usuario';
          set({ 
            isLoading: false, 
            error: errorMessage,
            isAuthenticated: false,
            user: null 
          });
          throw new Error(errorMessage);
        }
      },

      logout: () => {
        try {
          api.logout().catch(console.error); // Fire and forget
        } catch (error) {
          console.error('Error during logout:', error);
        } finally {
          clearAuthData();
          set({
            user: null,
            isAuthenticated: false,
            error: null
          });
        }
      },

      validateAuth: async () => {
        try {
          const token = getAuthToken();
          if (!token) {
            get().logout();
            return false;
          }

          const isValid = await api.validateToken();
          if (!isValid) {
            get().logout();
            return false;
          }

          return true;
        } catch (error) {
          console.error('Auth validation error:', error);
          get().logout();
          return false;
        }
      },

      clearError: () => {
        set({ error: null });
      },

      setLoading: (loading: boolean) => {
        set({ isLoading: loading });
      },

      initialize: () => {
        const userData = getUserData();
        const token = getAuthToken();
        
        if (userData && token) {
          set({
            user: userData,
            isAuthenticated: true
          });
          
          // Validate token in background
          get().validateAuth();
        }
      }
    }),
    {
      name: 'auction-auth-store',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
);