import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { linksApi, shareApi, uploadApi } from '@/lib/api';
import { CreateLinkPayload, UpdateLinkPayload, ShareLinkPayload } from '@/types';
import { useState } from 'react';

// ── Links ──
export function useLinks() {
  return useQuery({
    queryKey: ['links'],
    queryFn: linksApi.getAll,
  });
}

export function useCreateLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateLinkPayload) => linksApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['links'] });
    },
  });
}

export function useUpdateLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateLinkPayload }) =>
      linksApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['links'] });
    },
  });
}

export function useDeleteLink() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => linksApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['links'] });
    },
  });
}

// ── Share ──
export function useShareLink() {
  return useMutation({
    mutationFn: (data: ShareLinkPayload) => shareApi.shareLink(data),
  });
}

// ── Upload ──
export function useUploadFile() {
  const [progress, setProgress] = useState(0);

  const mutation = useMutation({
    mutationFn: (file: File) => uploadApi.uploadFile(file, setProgress),
    onSettled: () => setProgress(0),
  });

  return { ...mutation, progress };
}
