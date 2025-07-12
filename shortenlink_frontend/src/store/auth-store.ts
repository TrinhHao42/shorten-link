import { create } from 'zustand';
import { User } from '@/types';

const TOKEN_COOKIE_NAME = 'auth-storage';
const TOKEN_MAX_AGE = 604800; // 7 days

function setTokenCookie(token: string | null) {
  if (typeof document === 'undefined') return;
  if (token) {
    document.cookie = `${TOKEN_COOKIE_NAME}=${encodeURIComponent(token)}; path=/; max-age=${TOKEN_MAX_AGE}; SameSite=Lax`;
  } else {
    document.cookie = `${TOKEN_COOKIE_NAME}=; path=/; max-age=0`;
  }
}

function getTokenFromCookie(): string | null {
  if (typeof document === 'undefined') return null;
  const match = document.cookie.match(
    new RegExp(`(?:^|; )${TOKEN_COOKIE_NAME}=([^;]*)`)
  );
  return match ? decodeURIComponent(match[1]) : null;
}

interface AuthState {
  accessToken: string | null;
  isAuthenticated: boolean;
  user: User | null;
  setAuth: (accessToken: string) => void;
  setUser: (user: User | null) => void;
  logout: () => void;
  hydrate: () => void;
}

export const useAuthStore = create<AuthState>()((set) => ({
  accessToken: null,
  isAuthenticated: false,
  user: null,
  setAuth: (accessToken) => {
    setTokenCookie(accessToken);
    set({ accessToken, isAuthenticated: true });
    // Trigger user fetch (could be done via a side effect, but often done in components)
  },
  setUser: (user) => {
    set({ user });
  },
  logout: () => {
    setTokenCookie(null);
    set({ accessToken: null, isAuthenticated: false, user: null });
  },
  hydrate: () => {
    const token = getTokenFromCookie();
    if (token) {
      set({ accessToken: token, isAuthenticated: true });
    }
  },
}));

// Auto-hydrate from cookie on load
if (typeof window !== 'undefined') {
  useAuthStore.getState().hydrate();
}
