import { useState } from 'react';
import { AuthForm } from './components/AuthForm/AuthForm';
import { FlipTable } from './components/FlipTable/FlipTable';
import { usePrices } from './hooks/usePrices';
import { useSearch } from './hooks/useSearch';
import type { AuthResponse } from './types/auth';
import type { FlipOpportunity } from './types/prices';
import './App.css';

const TOKEN_KEY = 'osrs_token';
const USER_KEY  = 'osrs_user';

function getSavedAuth(): { username: string } | null {
  const username = localStorage.getItem(USER_KEY);
  return username ? { username } : null;
}

function App() {
  const [limit, setLimit]         = useState(50);
  const [minMargin, setMinMargin] = useState(100);
  const [search, setSearch]       = useState('');
  const [showAuth, setShowAuth]   = useState(false);
  const [auth, setAuth]           = useState<{ username: string } | null>(getSavedAuth);
  const [selected, setSelected]   = useState<FlipOpportunity | null>(null);

  const { data, loading, error }           = usePrices(limit, minMargin);
  const { results: searchResults, loading: searchLoading } = useSearch(search);

  const isSearching = search.trim().length > 0;

  // Remove search hits from the top-N list so items don't appear twice
  const searchResultIds = new Set(searchResults.map(i => i.id));
  const topFlips = isSearching ? data.filter(i => !searchResultIds.has(i.id)) : data;

  function handleAuthSuccess(res: AuthResponse) {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, res.username);
    setAuth({ username: res.username });
    setShowAuth(false);
  }

  function handleLogout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setAuth(null);
  }

  function handleSelectItem(item: FlipOpportunity) {
    setSelected(prev => prev?.id === item.id ? null : item);
  }

  function handleSearchChange(value: string) {
    setSearch(value);
    setSelected(null);
  }

  return (
    <div className="container">
      <header className="header">
        <div>
          <h1>OSRS GE Flipper</h1>
          <p className="subtitle">Live Grand Exchange flip opportunities, sorted by score</p>
        </div>
        <div className="authArea">
          {auth ? (
            <>
              <span className="username">Logged in as <strong>{auth.username}</strong></span>
              <button className="btnSecondary" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <button className="btnPrimary" onClick={() => setShowAuth(true)}>Log in / Register</button>
          )}
        </div>
      </header>

      <div className="controls">
        <label className="searchLabel">
          Search item
          <div className="searchWrapper">
            <input
              type="text"
              placeholder="e.g. Abyssal whip"
              value={search}
              onChange={e => handleSearchChange(e.target.value)}
            />
            {search && (
              <button className="searchClear" onClick={() => handleSearchChange('')}>✕</button>
            )}
          </div>
        </label>
        <label>
          Min Margin (gp)
          <input
            type="number"
            value={minMargin}
            min={0}
            step={100}
            onChange={e => setMinMargin(Math.max(0, Number(e.target.value)))}
          />
        </label>
        <label>
          Results
          <input
            type="number"
            value={limit}
            min={1}
            max={200}
            onChange={e => setLimit(Math.min(200, Math.max(1, Number(e.target.value))))}
          />
        </label>
      </div>

      {/* Search results section */}
      {isSearching && (
        <section>
          <p className="sectionLabel">
            {searchLoading ? 'Searching…' : `Results for "${search}"`}
          </p>
          {!searchLoading && searchResults.length === 0 && (
            <p className="status">No items found.</p>
          )}
          {!searchLoading && searchResults.length > 0 && (
            <FlipTable
              items={searchResults}
              selectedId={selected?.id ?? null}
              onSelect={handleSelectItem}
            />
          )}
          <div className="sectionDivider" />
        </section>
      )}

      {/* Top flips section */}
      {isSearching && <p className="sectionLabel">Top {limit} flips</p>}
      {loading && <p className="status">Loading prices…</p>}
      {error   && <p className="status error">Error: {error}</p>}
      {!loading && !error && (
        <FlipTable
          items={topFlips}
          selectedId={selected?.id ?? null}
          onSelect={handleSelectItem}
        />
      )}

      {showAuth && (
        <AuthForm onSuccess={handleAuthSuccess} onClose={() => setShowAuth(false)} />
      )}
    </div>
  );
}

export default App;
