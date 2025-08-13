import Cookies from 'js-cookie';
import { User, AuthResponse } from '@/types';

export const AUTH_TOKEN_KEY = 'auction_token';
export const USER_DATA_KEY = 'auction_user';

export function setAuthData(authResponse: AuthResponse) {
  // Set token cookie with security options
  Cookies.set(AUTH_TOKEN_KEY, authResponse.token, {
    expires: 1, // 1 day
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict'
  });

  // Set user data
  const userData = {
    email: authResponse.email,
    username: authResponse.username,
    role: authResponse.role
  };
  
  Cookies.set(USER_DATA_KEY, JSON.stringify(userData), {
    expires: 1,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'strict'
  });
}

export function getAuthToken(): string | null {
  return Cookies.get(AUTH_TOKEN_KEY) || null;
}

export function getUserData(): Partial<User> | null {
  try {
    const userData = Cookies.get(USER_DATA_KEY);
    return userData ? JSON.parse(userData) : null;
  } catch {
    return null;
  }
}

export function clearAuthData() {
  Cookies.remove(AUTH_TOKEN_KEY);
  Cookies.remove(USER_DATA_KEY);
}

export function isAuthenticated(): boolean {
  return !!getAuthToken();
}

export function hasRole(requiredRole: string): boolean {
  const userData = getUserData();
  if (!userData || !userData.role) return false;

  const roleHierarchy = {
    'PARTICIPANTE': 1,
    'MODERADOR': 2,
    'ADMINISTRADOR': 3
  };

  const userRoleLevel = roleHierarchy[userData.role as keyof typeof roleHierarchy] || 0;
  const requiredRoleLevel = roleHierarchy[requiredRole as keyof typeof roleHierarchy] || 0;

  return userRoleLevel >= requiredRoleLevel;
}

export function canModerateAuctions(): boolean {
  return hasRole('MODERADOR');
}

export function canAdministrateUsers(): boolean {
  return hasRole('ADMINISTRADOR');
}

export function formatUserDisplayName(user?: Partial<User>): string {
  if (!user) {
    const userData = getUserData();
    if (!userData) return 'Usuario';
    return userData.username || userData.email || 'Usuario';
  }
  
  if (user.firstName && user.lastName) {
    return `${user.firstName} ${user.lastName}`;
  }
  
  return user.username || user.email || 'Usuario';
}