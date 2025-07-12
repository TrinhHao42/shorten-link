// ── Generic API wrapper returned by server ──
export interface ApiResponse<T> {
  code: number;
  message: string;
  result: T;
}

export interface User {
  id: number;
  email: string;
  username: string;
  img?: string;
}

export interface Link {
  id: number;
  originalUrl: string;
  slug: string;
  type: string;
  clickCount: number;
}

export interface AuthResponse {
  accessToken: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
}

export interface UpdateProfilePayload {
  username: string;
}

export interface CreateLinkPayload {
  originalUrl: string;
  customSlug?: string;
  password?: string;
}

export interface UpdateLinkPayload {
  originalUrl?: string;
  slug?: string;
}

export interface ShareLinkPayload {
  shortLink: string;
  receiverEmail: string;
  message?: string;
}

export interface UploadResponse {
  slug: string;
  downloadUrl: string;
}
