import { apiBase, authHeaders } from './client';
import type { WatchlistItem } from '../types/watchlist';

export async function fetchWatchlist(): Promise<WatchlistItem[]> {
  const res = await fetch(`${apiBase}/api/watchlist`, { headers: authHeaders() });
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}

export async function addToWatchlist(itemId: number, itemName: string, icon: string): Promise<WatchlistItem> {
  const res = await fetch(`${apiBase}/api/watchlist`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ itemId, itemName, icon }),
  });
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}

export async function removeFromWatchlist(itemId: number): Promise<void> {
  const res = await fetch(`${apiBase}/api/watchlist/${itemId}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error(`Error ${res.status}`);
}
