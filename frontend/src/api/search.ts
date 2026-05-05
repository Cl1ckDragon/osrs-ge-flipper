import { apiBase } from './client';
import type { FlipOpportunity } from '../types/prices';

export async function fetchSearch(query: string, limit = 5): Promise<FlipOpportunity[]> {
  const res = await fetch(`${apiBase}/api/prices/search?q=${encodeURIComponent(query)}&limit=${limit}`);
  if (!res.ok) return [];
  return res.json() as Promise<FlipOpportunity[]>;
}
