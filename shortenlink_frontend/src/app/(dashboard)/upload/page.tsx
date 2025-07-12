import UploadFile from '@/components/UploadFile';
import { Upload } from 'lucide-react';

export default function UploadPage() {
  return (
    <div className="animate-fade-in mx-auto max-w-2xl space-y-6">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-violet-500/20">
          <Upload size={18} className="text-violet-400" />
        </div>
        <div>
          <h1 className="text-xl font-bold text-white">File Upload</h1>
          <p className="text-sm text-gray-500">
            Upload files and get shareable download links
          </p>
        </div>
      </div>

      <UploadFile />
    </div>
  );
}
