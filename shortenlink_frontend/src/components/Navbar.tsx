'use client';

import { useAuthStore } from '@/store/auth-store';
import { Bell, Search } from 'lucide-react';
import { useEffect } from 'react';
import { authApi } from '@/lib/api';
import Link from 'next/link';

export default function Navbar() {
  const { isAuthenticated, user, setUser } = useAuthStore();

  useEffect(() => {
    if (isAuthenticated && !user) {
      authApi.getMe().then(setUser).catch(console.error);
    }
  }, [isAuthenticated, user, setUser]);

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-white/10 bg-gray-950/80 px-6 backdrop-blur-xl">
      {/* Search */}
      <div className="relative max-w-md flex-1">
        <Search
          size={16}
          className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
        />
        <input
          type="text"
          placeholder="Search links..."
          className="w-full rounded-lg border border-white/10 bg-white/5 py-2 pl-9 pr-4 text-sm text-white placeholder-gray-500 outline-none transition-colors focus:border-violet-500/50 focus:ring-1 focus:ring-violet-500/25"
        />
      </div>

      {/* Right side */}
      <div className="flex items-center gap-4">
        <button className="relative rounded-lg p-2 text-gray-400 transition-colors hover:bg-white/5 hover:text-white">
          <Bell size={18} />
          <span className="absolute right-1.5 top-1.5 h-2 w-2 rounded-full bg-violet-500" />
        </button>

        <div className="h-6 w-px bg-white/10" />

        <Link href="/profile">
          <div className="flex items-center gap-3 cursor-pointer group">
            <div className="flex h-8 w-8 overflow-hidden items-center justify-center rounded-full bg-gradient-to-br from-violet-500 to-indigo-600 text-xs font-bold text-white transition-opacity group-hover:opacity-80">
              {user?.img ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={user.img} alt="Avatar" className="h-full w-full object-cover" />
              ) : isAuthenticated ? (
                user?.username?.charAt(0).toUpperCase() || 'U'
              ) : (
                '?'
              )}
            </div>
            <div className="hidden md:block">
              <p className="text-sm font-medium text-white group-hover:text-violet-400 transition-colors">
                {isAuthenticated ? user?.username || 'User' : 'Guest'}
              </p>
            </div>
          </div>
        </Link>
      </div>
    </header>
  );
}
