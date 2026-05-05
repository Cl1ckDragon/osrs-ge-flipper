import { useEffect, useState } from 'react';
import { fetchFlipOpportunities } from '../api/prices';
import type { FlipOpportunity } from '../types/prices';

export function usePrices(limit: number, minMargin: number) {
  const [data, setData] = useState<FlipOpportunity[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    fetchFlipOpportunities(limit, minMargin)
      .then(items => { if (!cancelled) setData(items); })
      .catch(err => { if (!cancelled) setError((err as Error).message); })
      .finally(() => { if (!cancelled) setLoading(false); });

    return () => { cancelled = true; };
  }, [limit, minMargin]);

  return { data, loading, error };
}
