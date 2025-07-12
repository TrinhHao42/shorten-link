'use client';

import { useState } from 'react';
import {
  Copy,
  ExternalLink,
  MoreVertical,
  Pencil,
  Trash2,
  Share2,
  BarChart3,
  Check,
} from 'lucide-react';
import { Link as LinkType } from '@/types';
import toast from 'react-hot-toast';
import { Card } from '@/components/ui/Card';

interface LinkCardProps {
  link: LinkType;
  onEdit: (link: LinkType) => void;
  onDelete: (id: number) => void;
  onShare: (link: LinkType) => void;
}

export default function LinkCard({ link, onEdit, onDelete, onShare }: LinkCardProps) {
  const [showMenu, setShowMenu] = useState(false);
  const [copied, setCopied] = useState(false);
  const origin = typeof window !== 'undefined' ? window.location.origin : '';
  const shortUrl = link.type === 'FILE'
    ? `${origin}/files/download/${link.slug}`
    : `${origin}/${link.slug}`;

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      toast.success('Link copied to clipboard!');
      setTimeout(() => setCopied(false), 2000);
    } catch {
      toast.error('Failed to copy');
    }
  };

  return (
    <Card variant="interactive" className="group relative p-5 duration-300">
      {/* Header */}
      <div className="mb-3 flex items-start justify-between">
        <div className="flex-1 space-y-1 overflow-hidden">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-violet-500/15">
              <ExternalLink size={14} className="text-violet-400" />
            </div>
            <a
              href={shortUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="h3 truncate text-sm font-semibold text-white"
            >
              {shortUrl}
            </a>
          </div>
          <p className="truncate pl-10 text-xs text-gray-500">
            {link.originalUrl}
          </p>
        </div>

        {/* Actions */}
        <div className="relative ml-2">
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="rounded-lg p-1.5 text-gray-500 transition-colors hover:bg-white/10 hover:text-white"
          >
            <MoreVertical size={16} />
          </button>
          {showMenu && (
            <>
              <div
                className="fixed inset-0 z-10"
                onClick={() => setShowMenu(false)}
              />
              <div className="absolute right-0 top-full z-20 mt-1 w-40 rounded-xl border border-white/10 bg-gray-900 p-1 shadow-xl shadow-black/40">
                <button
                  onClick={() => { onEdit(link); setShowMenu(false); }}
                  className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-gray-300 hover:bg-white/10 hover:text-white"
                >
                  <Pencil size={14} /> Edit
                </button>
                <button
                  onClick={() => { onShare(link); setShowMenu(false); }}
                  className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-gray-300 hover:bg-white/10 hover:text-white"
                >
                  <Share2 size={14} /> Share
                </button>
                <button
                  onClick={() => { onDelete(link.id); setShowMenu(false); }}
                  className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-sm text-red-400 hover:bg-red-500/10 hover:text-red-300"
                >
                  <Trash2 size={14} /> Delete
                </button>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Stats */}
      <div className="flex items-center gap-4 border-t border-white/5 pt-3">
        <div className="flex items-center gap-1.5 text-xs text-gray-500">
          <BarChart3 size={13} className="text-violet-400" />
          <span>{link.clickCount} clicks</span>
        </div>

        {/* Copy button */}
        <button
          onClick={handleCopy}
          className="ml-auto flex items-center gap-1.5 rounded-lg bg-violet-500/15 px-3 py-1.5 text-xs font-medium text-violet-400 transition-all hover:bg-violet-500/25"
        >
          {copied ? <Check size={13} /> : <Copy size={13} />}
          {copied ? 'Copied!' : 'Copy'}
        </button>
      </div>
    </Card>
  );
}
