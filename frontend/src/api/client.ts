export const apiBase = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');

export function authHeaders(): Record<string, string> {
  const token = localStorage.getItem('osrs_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}
