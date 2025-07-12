'use client';

import { useCallback, useState } from 'react';
import { Upload, FileUp, CheckCircle, X, File } from 'lucide-react';
import { useUploadFile } from '@/hooks/use-queries';
import toast from 'react-hot-toast';
import { Button } from '@/components/ui/Button';

export default function UploadFile() {
  const [dragActive, setDragActive] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const { mutateAsync: upload, progress, isPending, data } = useUploadFile();

  const handleDrag = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(e.type === 'dragenter' || e.type === 'dragover');
  }, []);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files?.[0]) {
      setSelectedFile(e.dataTransfer.files[0]);
    }
  }, []);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) return;
    try {
      await upload(selectedFile);
      toast.success('File uploaded successfully!');
    } catch {
      toast.error('Upload failed');
    }
  };

  const formatSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <div className="space-y-4">
      {/* Dropzone */}
      <div
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        className={`relative flex min-h-[240px] cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-dashed transition-all duration-300 ${
          dragActive
            ? 'border-violet-500 bg-violet-500/10'
            : 'border-white/10 bg-white/[0.02] hover:border-violet-500/30 hover:bg-white/[0.04]'
        }`}
      >
        <input
          type="file"
          onChange={handleFileChange}
          className="absolute inset-0 cursor-pointer opacity-0"
        />
        <div
          className={`mb-3 flex h-14 w-14 items-center justify-center rounded-2xl transition-colors ${
            dragActive ? 'bg-violet-500/20' : 'bg-white/5'
          }`}
        >
          <Upload
            size={24}
            className={dragActive ? 'text-violet-400' : 'text-gray-500'}
          />
        </div>
        <p className="mb-1 text-sm font-medium text-white">
          {dragActive ? 'Drop your file here' : 'Drag & drop or click to upload'}
        </p>
        <p className="text-xs text-gray-500">
          Any file type up to 50MB
        </p>
      </div>

      {/* Selected file info */}
      {selectedFile && !data && (
        <div className="flex items-center gap-3 rounded-xl border border-white/10 bg-white/[0.03] p-4">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-violet-500/15">
            <File size={18} className="text-violet-400" />
          </div>
          <div className="flex-1 overflow-hidden">
            <p className="truncate text-sm font-medium text-white">
              {selectedFile.name}
            </p>
            <p className="text-xs text-gray-500">
              {formatSize(selectedFile.size)}
            </p>
          </div>
          <button
            onClick={() => setSelectedFile(null)}
            className="rounded-lg p-1.5 text-gray-500 hover:bg-white/10 hover:text-white"
          >
            <X size={16} />
          </button>
        </div>
      )}

      {/* Progress */}
      {isPending && (
        <div className="space-y-2 rounded-xl border border-white/10 bg-white/[0.03] p-4">
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-400">Uploading…</span>
            <span className="font-medium text-violet-400">{progress}%</span>
          </div>
          <div className="h-2 overflow-hidden rounded-full bg-white/10">
            <div
              className="h-full rounded-full bg-gradient-to-r from-violet-500 to-indigo-500 transition-all duration-300"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      )}

      {/* Upload button */}
      {selectedFile && !data && !isPending && (
        <Button
          onClick={handleUpload}
          className="w-full"
        >
          <FileUp size={16} />
          Upload File
        </Button>
      )}

      {/* Success */}
      {data && (
        <div className="rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-4">
          <div className="mb-2 flex items-center gap-2">
            <CheckCircle size={18} className="text-emerald-400" />
            <span className="text-sm font-semibold text-emerald-400">
              Upload Complete!
            </span>
          </div>
          <p className="text-xs text-gray-400">Download link:</p>
          <a
            href={data.downloadUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-1 block truncate text-sm font-medium text-violet-400 underline decoration-violet-500/30 hover:decoration-violet-400"
          >
            {data.downloadUrl}
          </a>
        </div>
      )}
    </div>
  );
}
