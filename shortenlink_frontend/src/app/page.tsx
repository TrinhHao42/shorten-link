import Link from 'next/link';
import { ArrowRight, Zap, Shield, BarChart3, Globe } from 'lucide-react';
import Logo from '@/components/Logo';

export default function HomePage() {
  return (
    <div className="relative flex min-h-screen flex-col bg-gray-950">
      {/* Background glow */}
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute -top-[25%] left-1/2 h-[600px] w-[800px] -translate-x-1/2 rounded-full bg-violet-600/15 blur-[120px]" />
        <div className="absolute -bottom-[10%] left-[20%] h-[400px] w-[500px] rounded-full bg-indigo-600/10 blur-[100px]" />
      </div>

      {/* Nav */}
      <nav className="relative z-10 flex items-center justify-between px-6 py-5 md:px-12">
        <Logo imageSize={36} textSize="text-lg" />
        <div className="flex items-center gap-3">
          <Link
            href="/login"
            className="rounded-lg px-4 py-2 text-sm font-medium text-gray-400 transition-colors hover:text-white"
          >
            Sign In
          </Link>
          <Link
            href="/register"
            className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white transition-all hover:bg-violet-500 hover:shadow-lg hover:shadow-violet-500/25"
          >
            Get Started
          </Link>
        </div>
      </nav>

      {/* Hero */}
      <main className="relative z-10 flex flex-1 flex-col items-center justify-center px-6 text-center">
        <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-violet-500/20 bg-violet-500/10 px-4 py-1.5 text-xs font-medium text-violet-400">
          <Zap size={12} /> Supercharge Your Links
        </div>

        <h1 className="mb-4 max-w-3xl text-4xl font-extrabold leading-tight tracking-tight text-white md:text-6xl">
          Shorten, Share &{' '}
          <span className="bg-gradient-to-r from-violet-400 to-indigo-400 bg-clip-text text-transparent">
            Track Your Links
          </span>
        </h1>

        <p className="mb-8 max-w-xl text-base text-gray-500 md:text-lg">
          Create powerful short links with custom slugs, real-time analytics,
          file sharing, and seamless team collaboration.
        </p>

        <div className="flex flex-col items-center gap-3 sm:flex-row">
          <Link
            href="/register"
            className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-violet-600 to-indigo-600 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-violet-500/25 transition-all hover:shadow-violet-500/40"
          >
            Start Shortening <ArrowRight size={16} />
          </Link>
          <Link
            href="/login"
            className="flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-6 py-3 text-sm font-medium text-gray-300 transition-all hover:bg-white/10 hover:text-white"
          >
            Sign In
          </Link>
        </div>

        {/* Features */}
        <div className="mt-20 grid max-w-4xl gap-4 md:grid-cols-3">
          {[
            {
              icon: Globe,
              title: 'Custom Slugs',
              desc: 'Create memorable short links with your own custom slugs.',
            },
            {
              icon: BarChart3,
              title: 'Click Analytics',
              desc: 'Track every click with real-time analytics dashboard.',
            },
            {
              icon: Shield,
              title: 'Secure & Fast',
              desc: 'Enterprise-grade security with blazing fast redirects.',
            },
          ].map((f, i) => (
            <div
              key={i}
              className="rounded-xl border border-white/[0.06] bg-white/[0.02] p-5 text-left transition-all hover:border-violet-500/20 hover:bg-white/[0.04]"
            >
              <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-violet-500/15">
                <f.icon size={18} className="text-violet-400" />
              </div>
              <h3 className="mb-1 text-sm font-semibold text-white">
                {f.title}
              </h3>
              <p className="text-xs text-gray-500">{f.desc}</p>
            </div>
          ))}
        </div>
      </main>

      {/* Footer */}
      <footer className="relative z-10 border-t border-white/[0.06] py-6 text-center text-xs text-gray-600">
        © {new Date().getFullYear()} ShortenLink. All rights reserved.
      </footer>
    </div>
  );
}
