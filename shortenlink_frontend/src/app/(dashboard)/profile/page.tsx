'use client';

import { useState, useRef, useEffect } from 'react';
import { useAuthStore } from '@/store/auth-store';
import { usersApi } from '@/lib/api';
import toast from 'react-hot-toast';
import { Upload, Save, Loader2, User as UserIcon } from 'lucide-react';

export default function ProfilePage() {
  const { user, setUser } = useAuthStore();
  const [username, setUsername] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (user) {
      setUsername(user.username || '');
    }
  }, [user]);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) return;

    setIsSaving(true);
    try {
      const updatedUser = await usersApi.updateProfile({ username });
      setUser(updatedUser);
      toast.success('Profile updated successfully');
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      toast.error((error as any).response?.data?.message || 'Failed to update profile');
    } finally {
      setIsSaving(false);
    }
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    try {
      const updatedUser = await usersApi.uploadAvatar(file);
      setUser(updatedUser);
      toast.success('Avatar updated successfully');
    } catch (error) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      toast.error((error as any).response?.data?.message || 'Failed to upload avatar');
    } finally {
      setIsUploading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  return (
    <div className="mx-auto max-w-2xl px-6 py-12">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">Profile Settings</h1>
        <p className="mt-2 text-gray-400">
          Manage your account details and personalization.
        </p>
      </div>

      <div className="rounded-xl border border-white/10 bg-gray-950/50 backdrop-blur-xl p-8 shadow-2xl">
        <div className="flex flex-col md:flex-row gap-10 items-start">
          
          {/* Avatar Section */}
          <div className="flex flex-col items-center gap-4">
            <div className="relative group h-32 w-32 shrink-0 overflow-hidden rounded-full border-4 border-white/5 bg-gradient-to-br from-violet-500/20 to-indigo-600/20 transition-all hover:border-violet-500/50">
              {user?.img ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={user.img} alt="Avatar" className="h-full w-full object-cover" />
              ) : (
                <div className="flex h-full w-full items-center justify-center text-4xl font-bold text-white">
                  {user?.username?.charAt(0).toUpperCase() || <UserIcon size={48} className="text-gray-500" />}
                </div>
              )}
              <button 
                onClick={() => fileInputRef.current?.click()}
                disabled={isUploading}
                type="button"
                className="absolute inset-0 flex flex-col items-center justify-center bg-black/60 opacity-0 transition-opacity focus:opacity-100 group-hover:opacity-100"
              >
                {isUploading ? (
                  <Loader2 className="animate-spin text-white mb-1" size={24} />
                ) : (
                  <>
                    <Upload className="text-white mb-1" size={24} />
                    <span className="text-xs font-medium text-white">Change</span>
                  </>
                )}
              </button>
            </div>
            <input 
              type="file" 
              ref={fileInputRef} 
              onChange={handleFileChange} 
              accept="image/*" 
              className="hidden" 
            />
            <div className="text-center">
              <h3 className="text-sm font-medium text-white">Profile Photo</h3>
              <p className="text-xs text-gray-400 mt-1">Max file size 5MB</p>
            </div>
          </div>

          {/* Profile Form */}
          <form onSubmit={handleSaveProfile} className="flex-1 w-full space-y-6">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-300">
                Username
              </label>
              <div className="mt-2 relative">
                <input
                  type="text"
                  id="username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="block w-full rounded-lg border border-white/10 bg-white/5 px-4 py-2.5 text-white placeholder-gray-500 outline-none transition-all focus:border-violet-500 focus:ring-1 focus:ring-violet-500/50"
                  placeholder="Enter a unique username"
                  required
                  minLength={3}
                  maxLength={50}
                  pattern="^[a-zA-Z0-9_-]+$"
                  title="Alphanumeric characters, underscores, and hyphens only"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300">
                Email Address
              </label>
              <div className="mt-2">
                <input
                  type="email"
                  value={user?.email || ''}
                  disabled
                  className="block w-full rounded-lg border border-white/5 bg-white/5 px-4 py-2.5 text-gray-500 cursor-not-allowed"
                />
                <p className="mt-1.5 text-xs text-gray-500">
                  Email addresses cannot be changed at this moment.
                </p>
              </div>
            </div>

            <div className="flex justify-end pt-4">
              <button
                type="submit"
                disabled={isSaving || !username?.trim() || username === user?.username}
                className="flex items-center gap-2 rounded-lg bg-violet-600 px-6 py-2.5 text-sm font-semibold text-white shadow-lg transition-all hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-violet-500/50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isSaving ? (
                  <Loader2 className="animate-spin" size={18} />
                ) : (
                  <Save size={18} />
                )}
                <span>Save Changes</span>
              </button>
            </div>
          </form>

        </div>
      </div>
    </div>
  );
}
