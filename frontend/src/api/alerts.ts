import { apiBase, authHeaders } from './client';
import type { PriceAlert } from '../types/alert';

export async function fetchAlerts(): Promise<PriceAlert[]> {
  const res = await fetch(`${apiBase}/api/alerts`, { headers: authHeaders() });
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}

export async function createAlert(
  itemId: number, itemName: string, icon: string, targetMargin: number
): Promise<PriceAlert> {
  const res = await fetch(`${apiBase}/api/alerts`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ itemId, itemName, icon, targetMargin }),
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error((data as { message?: string }).message ?? `Error ${res.status}`);
  return data;
}

export async function deleteAlert(id: number): Promise<void> {
  const res = await fetch(`${apiBase}/api/alerts/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error(`Error ${res.status}`);
}

export async function dismissAlert(id: number): Promise<PriceAlert> {
  const res = await fetch(`${apiBase}/api/alerts/${id}/dismiss`, {
    method: 'PATCH',
    headers: authHeaders(),
  });
  if (!res.ok) throw new Error(`Error ${res.status}`);
  return res.json();
}
