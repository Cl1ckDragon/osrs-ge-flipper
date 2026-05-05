import { useCallback, useEffect, useState } from 'react';
import { createAlert, deleteAlert, dismissAlert, fetchAlerts } from '../api/alerts';
import type { PriceAlert } from '../types/alert';

export function useAlerts(loggedIn: boolean) {
  const [alerts, setAlerts]     = useState<PriceAlert[]>([]);
  const [loading, setLoading]   = useState(false);

  const refresh = useCallback(() => {
    if (!loggedIn) return;
    setLoading(true);
    fetchAlerts()
      .then(setAlerts)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [loggedIn]);

  useEffect(() => { refresh(); }, [refresh]);

  const create = useCallback(async (
    itemId: number, itemName: string, icon: string, targetMargin: number
  ) => {
    const alert = await createAlert(itemId, itemName, icon, targetMargin);
    setAlerts(prev => [alert, ...prev]);
  }, []);

  const remove = useCallback(async (id: number) => {
    await deleteAlert(id);
    setAlerts(prev => prev.filter(a => a.id !== id));
  }, []);

  const dismiss = useCallback(async (id: number) => {
    const updated = await dismissAlert(id);
    setAlerts(prev => prev.map(a => a.id === id ? updated : a));
  }, []);

  const unreadCount = alerts.filter(a => a.triggered && !a.dismissed).length;

  return { alerts, loading, unreadCount, create, remove, dismiss, refresh };
}
