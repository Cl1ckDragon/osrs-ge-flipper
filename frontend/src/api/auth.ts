import type { AuthResponse } from '../types/auth';

async function post<T>(url: string, body: unknown): Promise<T> {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error((data as { message?: string }).message ?? `Error ${res.status}`);
  return data as T;
}

export const registerUser = (username: string, email: string, password: string) =>
  post<AuthResponse>('/api/auth/register', { username, email, password });

export const loginUser = (username: string, password: string) =>
  post<AuthResponse>('/api/auth/login', { username, password });
