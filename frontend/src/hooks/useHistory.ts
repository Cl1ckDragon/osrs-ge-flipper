import { useEffect, useState } from 'react';
import { fetchHistory } from '../api/history';
import type { PriceHistory } from '../types/history';

export function useHistory(itemId: number | null, hours = 24) {
  const [data, setData]       = useState<PriceHistory[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState<string | null>(null);

  useEffect(() => {
    if (itemId === null) { setData([]); return; }

    let cancelled = false;
    setLoading(true);
    setError(null);

    fetchHistory(itemId, hours)
      .then(d  => { if (!cancelled) setData(d); })
      .catch(e => { if (!cancelled) setError((e as Error).message); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [itemId, hours]);

  return { data, loading, error };
}
