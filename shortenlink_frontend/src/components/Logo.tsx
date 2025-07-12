import Image from 'next/image';
import Link from 'next/link';

interface LogoProps {
  className?: string;
  imageSize?: number;
  textSize?: string;
  href?: string;
}

export default function Logo({
  className = '',
  imageSize = 32,
  textSize = 'text-lg',
  href = '/',
}: LogoProps) {
  return (
    <Link href={href} className={`flex items-center gap-2 ${className}`}>
      <Image
        src="/logo.svg"
        alt="ShortenLink Logo"
        width={imageSize}
        height={imageSize}
        priority
      />
      <span className={`${textSize} font-bold tracking-tight text-white`}>
        Shorten<span className="text-violet-400">Link</span>
      </span>
    </Link>
  );
}
