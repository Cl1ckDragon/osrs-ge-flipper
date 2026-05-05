import type { FlipOpportunity } from '../types/prices';

export async function fetchFlipOpportunities(
  limit = 50,
  minMargin = 100
): Promise<FlipOpportunity[]> {
  const res = await fetch(`/api/prices?limit=${limit}&minMargin=${minMargin}`);
  if (!res.ok) throw new Error(`API error ${res.status}: ${res.statusText}`);
  return res.json() as Promise<FlipOpportunity[]>;
}
