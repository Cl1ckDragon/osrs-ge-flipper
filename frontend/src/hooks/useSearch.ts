import { useEffect, useState } from 'react';
import { fetchSearch } from '../api/search';
import type { FlipOpportunity } from '../types/prices';

export function useSearch(query: string) {
  const [results, setResults]   = useState<FlipOpportunity[]>([]);
  const [loading, setLoading]   = useState(false);

  useEffect(() => {
    if (!query.trim()) {
      setResults([]);
      return;
    }

    // 400ms debounce — don't hit the API on every keystroke
    const timer = setTimeout(() => {
      setLoading(true);
      fetchSearch(query)
        .then(setResults)
        .catch(() => setResults([]))
        .finally(() => setLoading(false));
    }, 400);

    return () => clearTimeout(timer);
  }, [query]);

  return { results, loading };
}
