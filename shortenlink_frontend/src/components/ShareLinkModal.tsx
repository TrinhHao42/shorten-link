'use client';

import { useState, useEffect } from 'react';
import { Send, Mail, MessageSquare, Download } from 'lucide-react';
import QRCode from 'qrcode';
import { useShareLink } from '@/hooks/use-queries';
import { Link as LinkType } from '@/types';
import toast from 'react-hot-toast';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface ShareLinkModalProps {
  link: LinkType;
  onClose: () => void;
}

export default function ShareLinkModal({ link, onClose }: ShareLinkModalProps) {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [qrCodeDataUrl, setQrCodeDataUrl] = useState<string>('');
  const shareLink = useShareLink();

  const shortUrl = `${typeof window !== 'undefined' ? window.location.origin : ''}/${link.slug}`;

  useEffect(() => {
    QRCode.toDataURL(shortUrl, { width: 160, margin: 1 })
      .then((url) => setQrCodeDataUrl(url))
      .catch(console.error);
  }, [shortUrl]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await shareLink.mutateAsync({
        shortLink: shortUrl,
        receiverEmail: email,
        message: message || undefined,
      });
      toast.success('Link shared successfully!');
      onClose();
    } catch {
      toast.error('Failed to share link');
    }
  };

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title="Share Link"
      description="Send this link via email"
      icon={<Send size={18} className="text-violet-400" />}
    >
      <div className="mt-2">
        {/* Link preview & QR */}
        <div className="mb-4 flex flex-col items-center rounded-lg border border-white/10 bg-white/5 p-4 text-sm text-gray-400">
          {qrCodeDataUrl && (
            <div className="mb-4 flex flex-col items-center">
              <div className="mb-3 overflow-hidden rounded-xl bg-white p-2">
                <img src={qrCodeDataUrl} alt="QR Code" width={140} height={140} />
              </div>
              <a
                href={qrCodeDataUrl}
                download={`qr-${link.slug}.png`}
                className="flex items-center gap-1.5 rounded-lg bg-violet-500/10 px-3 py-1.5 text-xs font-medium text-violet-400 transition-colors hover:bg-violet-500/20 hover:text-violet-300"
              >
                <Download size={14} />
                Download QR
              </a>
            </div>
          )}
          <div className="w-full truncate text-center">
            /{link.slug} → <span className="text-gray-500">{link.originalUrl}</span>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 mt-4">
          <Input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Receiver's email"
            required
            iconLeft={<Mail size={16} />}
          />

          <div className="relative">
            <MessageSquare
              size={16}
              className="absolute left-3 top-3 text-gray-500"
            />
            <textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Add a message (optional)"
              rows={3}
              className="w-full resize-none rounded-lg border border-white/10 bg-white/5 py-2.5 pl-9 pr-4 text-sm text-white placeholder-gray-600 outline-none transition-all focus:border-violet-500/50 focus:ring-1 focus:ring-violet-500/25"
            />
          </div>

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
              isLoading={shareLink.isPending}
            >
              <span className="flex items-center justify-center gap-2">
                <Send size={14} />
                Share
              </span>
            </Button>
          </div>
        </form>
      </div>
    </Modal>
  );
}
