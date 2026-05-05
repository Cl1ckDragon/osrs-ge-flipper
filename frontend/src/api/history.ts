import { apiBase } from './client';
import type { PriceHistory } from '../types/history';

export async function fetchHistory(itemId: number, hours = 24): Promise<PriceHistory[]> {
  const res = await fetch(`${apiBase}/api/prices/history/${itemId}?hours=${hours}`);
  if (!res.ok) throw new Error(`API error ${res.status}`);
  return res.json() as Promise<PriceHistory[]>;
}
