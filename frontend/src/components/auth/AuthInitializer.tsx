'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';

const AuthInitializer = () => {
  const initialize = useAuthStore(state => state.initialize);

  useEffect(() => {
    initialize();
  }, [initialize]);

  return null;
};

export default AuthInitializer;