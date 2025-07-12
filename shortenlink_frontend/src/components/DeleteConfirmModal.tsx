'use client';

import { AlertTriangle } from 'lucide-react';
import { useDeleteLink } from '@/hooks/use-queries';
import toast from 'react-hot-toast';
import { Modal } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';

interface DeleteConfirmModalProps {
  linkId: number;
  onClose: () => void;
}

export default function DeleteConfirmModal({
  linkId,
  onClose,
}: DeleteConfirmModalProps) {
  const deleteLink = useDeleteLink();

  const handleDelete = async () => {
    try {
      await deleteLink.mutateAsync(linkId);
      toast.success('Link deleted');
      onClose();
    } catch {
      toast.error('Failed to delete');
    }
  };

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title="Delete Link?"
      description="This action cannot be undone. The short link will stop working."
      icon={<AlertTriangle size={18} className="text-red-400" />}
      className="max-w-sm"
    >
      <div className="flex gap-2 pt-2">
        <Button
          variant="secondary"
          className="flex-1"
          onClick={onClose}
        >
          Cancel
        </Button>
        <Button
          variant="danger"
          className="flex-1"
          onClick={handleDelete}
          isLoading={deleteLink.isPending}
        >
          Delete
        </Button>
      </div>
    </Modal>
  );
}
