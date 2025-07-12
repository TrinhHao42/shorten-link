'use client';

import { useState } from 'react';
import { Link2, TrendingUp, MousePointerClick, Plus } from 'lucide-react';
import { useLinks } from '@/hooks/use-queries';
import { Link as LinkType } from '@/types';
import LinkCard from '@/components/LinkCard';
import CreateLinkForm from '@/components/CreateLinkForm';
import EditLinkModal from '@/components/EditLinkModal';
import DeleteConfirmModal from '@/components/DeleteConfirmModal';
import ShareLinkModal from '@/components/ShareLinkModal';

export default function DashboardPage() {
  const { data: links, isLoading } = useLinks();
  const [editingLink, setEditingLink] = useState<LinkType | null>(null);
  const [deletingLinkId, setDeletingLinkId] = useState<number | null>(null);
  const [sharingLink, setSharingLink] = useState<LinkType | null>(null);
  const [showCreate, setShowCreate] = useState(false);

  const totalClicks =
    links?.reduce((sum, l) => sum + l.clickCount, 0) ?? 0;

  return (
    <div className="animate-fade-in space-y-6">
      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        {[
          {
            label: 'Total Links',
            value: links?.length ?? 0,
            icon: Link2,
            color: 'violet',
          },
          {
            label: 'Total Clicks',
            value: totalClicks,
            icon: MousePointerClick,
            color: 'indigo',
          },
          {
            label: 'Avg Clicks/Link',
            value: links?.length ? Math.round(totalClicks / links.length) : 0,
            icon: TrendingUp,
            color: 'emerald',
          },
        ].map((stat, i) => (
          <div
            key={i}
            className="rounded-xl border border-white/[0.06] bg-white/[0.02] p-5"
          >
            <div className="mb-3 flex items-center justify-between">
              <span className="text-xs font-medium uppercase tracking-wider text-gray-500">
                {stat.label}
              </span>
              <div
                className={`flex h-8 w-8 items-center justify-center rounded-lg bg-${stat.color}-500/15`}
              >
                <stat.icon size={15} className={`text-${stat.color}-400`} />
              </div>
            </div>
            <p className="text-2xl font-bold text-white">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Create link */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-white">Your Links</h2>
          <p className="text-sm text-gray-500">Manage and share your shortened URLs</p>
        </div>
        <button
          onClick={() => setShowCreate(!showCreate)}
          className="flex items-center gap-2 rounded-lg bg-gradient-to-r from-violet-600 to-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-lg shadow-violet-500/25 transition-all hover:shadow-violet-500/40"
        >
          <Plus size={16} />
          New Link
        </button>
      </div>

      {showCreate && <CreateLinkForm />}

      {/* Links list */}
      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <div
              key={i}
              className="h-28 animate-shimmer rounded-xl border border-white/[0.06]"
            />
          ))}
        </div>
      ) : links && links.length > 0 ? (
        <div className="grid gap-4 md:grid-cols-2">
          {links.map((link) => (
            <LinkCard
              key={link.id}
              link={link}
              onEdit={setEditingLink}
              onDelete={setDeletingLinkId}
              onShare={setSharingLink}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-white/10 py-16">
          <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-2xl bg-white/5">
            <Link2 size={24} className="text-gray-600" />
          </div>
          <p className="mb-1 text-sm font-medium text-gray-400">
            No links yet
          </p>
          <p className="text-xs text-gray-600">
            Create your first short link above
          </p>
        </div>
      )}

      {/* Modals */}
      {editingLink && (
        <EditLinkModal
          link={editingLink}
          onClose={() => setEditingLink(null)}
        />
      )}
      {deletingLinkId && (
        <DeleteConfirmModal
          linkId={deletingLinkId}
          onClose={() => setDeletingLinkId(null)}
        />
      )}
      {sharingLink && (
        <ShareLinkModal
          link={sharingLink}
          onClose={() => setSharingLink(null)}
        />
      )}
    </div>
  );
}
