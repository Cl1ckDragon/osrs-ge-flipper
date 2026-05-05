import { useCallback, useEffect, useState } from 'react';
import { addToWatchlist, fetchWatchlist, removeFromWatchlist } from '../api/watchlist';
import type { WatchlistItem } from '../types/watchlist';

export function useWatchlist(loggedIn: boolean) {
  const [items, setItems]       = useState<WatchlistItem[]>([]);
  const [ids, setIds]           = useState<Set<number>>(new Set());
  const [loading, setLoading]   = useState(false);

  const refresh = useCallback(() => {
    if (!loggedIn) return;
    setLoading(true);
    fetchWatchlist()
      .then(data => {
        setItems(data);
        setIds(new Set(data.map(i => i.itemId)));
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [loggedIn]);

  useEffect(() => { refresh(); }, [refresh]);

  const toggle = useCallback(async (itemId: number, itemName: string, icon: string) => {
    if (ids.has(itemId)) {
      await removeFromWatchlist(itemId);
      setIds(prev => { const s = new Set(prev); s.delete(itemId); return s; });
      setItems(prev => prev.filter(i => i.itemId !== itemId));
    } else {
      const added = await addToWatchlist(itemId, itemName, icon);
      setIds(prev => new Set([...prev, itemId]));
      setItems(prev => [added, ...prev]);
    }
  }, [ids]);

  return { items, ids, loading, toggle, refresh };
}
