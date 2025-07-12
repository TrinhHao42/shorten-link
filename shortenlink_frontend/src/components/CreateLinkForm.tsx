'use client';

import { useState } from 'react';
import { Link2, Sparkles, ArrowRight, Check, Copy } from 'lucide-react';
import { useCreateLink } from '@/hooks/use-queries';
import toast from 'react-hot-toast';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardHeader, CardContent } from '@/components/ui/Card';

export default function CreateLinkForm() {
  const [url, setUrl] = useState('');
  const [slug, setSlug] = useState('');
  const [generatedLink, setGeneratedLink] = useState('');
  const [copied, setCopied] = useState(false);

  const createLink = useCreateLink();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!url) return;

    try {
      const link = await createLink.mutateAsync({
        originalUrl: url,
        customSlug: slug || undefined,
      });

      const shortUrl = `${window.location.origin}/${link.slug}`;
      setGeneratedLink(shortUrl);
      setUrl('');
      setSlug('');
      toast.success('Short link created!');
    } catch {
      toast.error('Failed to create link');
    }
  };

  const handleCopy = async () => {
    await navigator.clipboard.writeText(generatedLink);
    setCopied(true);
    toast.success('Copied!');
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <Card variant="gradient">
      <CardHeader>
        <div className="flex items-center gap-2">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-violet-500/20">
            <Sparkles size={16} className="text-violet-400" />
          </div>
          <div>
            <h2 className="text-base font-semibold text-white">Create Short Link</h2>
            <p className="text-xs text-gray-500">Paste your long URL below</p>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-3">
          <Input
            type="url"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="https://example.com/very-long-url"
            required
            iconLeft={<Link2 size={16} />}
          />

          <Input
            type="text"
            value={slug}
            onChange={(e) => setSlug(e.target.value)}
            placeholder="Custom slug (optional)"
          />

          <Button
            type="submit"
            isLoading={createLink.isPending}
            className="w-full"
          >
            Shorten URL <ArrowRight size={14} />
          </Button>
        </form>

        {/* Generated link */}
        {generatedLink && (
          <div className="mt-4 flex items-center gap-2 rounded-lg border border-emerald-500/20 bg-emerald-500/10 p-3">
            <div className="flex-1 truncate text-sm font-medium text-emerald-400">
              {generatedLink}
            </div>
            <button
              onClick={handleCopy}
              className="flex shrink-0 items-center gap-1 rounded-lg bg-emerald-500/20 px-3 py-1.5 text-xs font-medium text-emerald-400 transition-colors hover:bg-emerald-500/30"
            >
              {copied ? <Check size={13} /> : <Copy size={13} />}
              {copied ? 'Copied' : 'Copy'}
            </button>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
