import api from '@/lib/axios';
import {
  ApiResponse,
  AuthResponse,
  LoginPayload,
  RegisterPayload,
  Link,
  CreateLinkPayload,
  UpdateLinkPayload,
  ShareLinkPayload,
  UploadResponse,
  User,
  UpdateProfilePayload,
} from '@/types';

// ── Auth ──
export const authApi = {
  login: (data: LoginPayload) =>
    api
      .post<ApiResponse<AuthResponse>>('/auth/login', data)
      .then((r) => r.data.result),

  register: (data: RegisterPayload) =>
    api
      .post<ApiResponse<AuthResponse>>('/auth/register', data)
      .then((r) => r.data.result),

  getMe: () =>
    api
      .get<ApiResponse<User>>('/auth/me')
      .then((r) => r.data.result),
};

// ── Users ──
export const usersApi = {
  updateProfile: (data: UpdateProfilePayload) =>
    api
      .put<ApiResponse<User>>('/users/me/profile', data)
      .then((r) => r.data.result),

  uploadAvatar: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api
      .post<ApiResponse<User>>('/users/me/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data.result);
  },
};

// ── Links ──
export const linksApi = {
  getAll: () =>
    api
      .get<ApiResponse<Link[]>>('/links')
      .then((r) => r.data.result),

  getById: (id: number) =>
    api
      .get<ApiResponse<Link>>(`/links/${id}`)
      .then((r) => r.data.result),

  create: (data: CreateLinkPayload) =>
    api
      .post<ApiResponse<Link>>('/links', data)
      .then((r) => r.data.result),

  update: (id: number, data: UpdateLinkPayload) =>
    api
      .put<ApiResponse<Link>>(`/links/${id}`, data)
      .then((r) => r.data.result),

  delete: (id: number) =>
    api
      .delete<ApiResponse<void>>(`/links/${id}`)
      .then((r) => r.data.result),
};

// ── Share ──
export const shareApi = {
  shareLink: (data: ShareLinkPayload) =>
    api
      .post<ApiResponse<void>>('/share-link', data)
      .then((r) => r.data.result),
};

// ── Upload ──
export const uploadApi = {
  uploadFile: (
    file: File,
    onProgress?: (percent: number) => void,
    password?: string
  ): Promise<UploadResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    if (password) {
      formData.append('password', password);
    }

    return api
      .post<ApiResponse<UploadResponse>>('/files/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total && onProgress) {
            const percent = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            onProgress(percent);
          }
        },
      })
      .then((r) => r.data.result);
  },
};
