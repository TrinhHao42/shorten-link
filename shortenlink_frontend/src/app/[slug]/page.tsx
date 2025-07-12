import { redirect } from 'next/navigation';

interface SlugPageProps {
  params: Promise<{ slug: string }>;
}

export default async function SlugPage({ params }: SlugPageProps) {
  const { slug } = await params;

  // The server handles the redirect at GET /{slug} with a 302 response.
  // We redirect the user to the server URL directly.
  const serverUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  redirect(`${serverUrl}/${slug}`);
}
