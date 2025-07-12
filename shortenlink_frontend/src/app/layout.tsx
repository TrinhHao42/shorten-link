import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import ReactQueryProvider from '@/lib/react-query-provider';
import ClientToaster from '@/components/ClientToaster';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'ShortenLink — Modern URL Shortener',
  description:
    'Create, manage, and share shortened URLs with powerful analytics and file uploads.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className={inter.className}>
        <ReactQueryProvider>
          {children}
          <ClientToaster />
        </ReactQueryProvider>
      </body>
    </html>
  );
}
