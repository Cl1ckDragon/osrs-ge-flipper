// Empty string = relative URLs (local Docker / dev server).
// Set VITE_API_BASE_URL to the backend Render URL in production.
export const apiBase = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
