'use client';

import { useState } from 'react';
import { Save, Link2, Globe } from 'lucide-react';
import { Link as LinkType, UpdateLinkPayload } from '@/types';
import { useUpdateLink } from '@/hooks/use-queries';
import toast from 'react-hot-toast';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface EditLinkModalProps {
  link: LinkType;
  onClose: () => void;
}

export default function EditLinkModal({ link, onClose }: EditLinkModalProps) {
  const [slug, setSlug] = useState(link.slug);
  const [url, setUrl] = useState(link.originalUrl);
  const updateLink = useUpdateLink();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const data: UpdateLinkPayload = {};
    if (slug !== link.slug) data.slug = slug;
    if (url !== link.originalUrl) data.originalUrl = url;

    try {
      await updateLink.mutateAsync({ id: link.id, data });
      toast.success('Link updated!');
      onClose();
    } catch {
      toast.error('Failed to update link');
    }
  };

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title="Edit Link"
      description="Update link details"
      icon={<Save size={18} className="text-violet-400" />}
    >
      <form onSubmit={handleSubmit} className="space-y-4 pt-2">
        <Input
          label="Custom Slug"
          type="text"
          value={slug}
          onChange={(e) => setSlug(e.target.value)}
          iconLeft={<Link2 size={16} />}
        />

        <Input
          label="Original URL"
          type="url"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          iconLeft={<Globe size={16} />}
        />

        <div className="flex gap-2 pt-2">
          <Button
            type="button"
            variant="secondary"
            className="flex-1"
            onClick={onClose}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            className="flex-1 block text-center"
            isLoading={updateLink.isPending}
          >
            <span className="flex items-center justify-center gap-2">
              <Save size={14} />
              Save Changes
            </span>
          </Button>
        </div>
      </form>
    </Modal>
  );
}
