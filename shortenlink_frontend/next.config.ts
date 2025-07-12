import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/files/download/:path*',
        destination: 'http://localhost:8080/files/download/:path*',
      },
    ];
  },
};

export default nextConfig;
