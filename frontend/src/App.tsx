import { useState } from 'react';
import { AlertsPanel } from './components/AlertsPanel/AlertsPanel';
import { AuthForm } from './components/AuthForm/AuthForm';
import { CreateAlertModal } from './components/CreateAlertModal/CreateAlertModal';
import { FlipTable } from './components/FlipTable/FlipTable';
import { WatchlistPanel } from './components/WatchlistPanel/WatchlistPanel';
import { useAlerts } from './hooks/useAlerts';
import { usePrices } from './hooks/usePrices';
import { useSearch } from './hooks/useSearch';
import { useStats } from './hooks/useStats';
import { useWatchlist } from './hooks/useWatchlist';
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
  const [limit, setLimit]           = useState(50);
  const [minMargin, setMinMargin]   = useState(100);
  const [search, setSearch]         = useState('');
  const [showAuth, setShowAuth]     = useState(false);
  const [auth, setAuth]             = useState<{ username: string } | null>(getSavedAuth);
  const [selected, setSelected]     = useState<FlipOpportunity | null>(null);
  const [showWatchlist, setShowWatchlist] = useState(false);
  const [showAlerts, setShowAlerts]       = useState(false);
  const [alertTarget, setAlertTarget]     = useState<FlipOpportunity | null>(null);

  const registeredUsers = useStats();

  const { data, loading, error }                          = usePrices(limit, minMargin);
  const { results: searchResults, loading: searchLoading} = useSearch(search);
  const { items: watchlistItems, ids: watchlistIds, loading: watchlistLoading, toggle: toggleWatchlist }
    = useWatchlist(!!auth);
  const { alerts, loading: alertsLoading, unreadCount, create: createAlert, remove: removeAlert, dismiss: dismissAlert }
    = useAlerts(!!auth);

  const isSearching      = search.trim().length > 0;
  const searchResultIds  = new Set(searchResults.map(i => i.id));
  const topFlips         = isSearching ? data.filter(i => !searchResultIds.has(i.id)) : data;

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
    setShowWatchlist(false);
    setShowAlerts(false);
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
          {registeredUsers !== null && (
            <p className="userCount">● {registeredUsers.toLocaleString()} registered traders</p>
          )}
        </div>
        <div className="authArea">
          {auth ? (
            <>
              <span className="username">Logged in as <strong>{auth.username}</strong></span>
              <button
                className={showWatchlist ? 'btnActive' : 'btnSecondary'}
                onClick={() => { setShowWatchlist(v => !v); setShowAlerts(false); }}
              >
                ⭐ Watchlist
              </button>
              <button
                className={showAlerts ? 'btnActive' : 'btnSecondary'}
                onClick={() => { setShowAlerts(v => !v); setShowWatchlist(false); }}
              >
                🔔 Alerts{unreadCount > 0 && <span className="badge">{unreadCount}</span>}
              </button>
              <button className="btnSecondary" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <button className="btnPrimary" onClick={() => setShowAuth(true)}>Log in / Register</button>
          )}
        </div>
      </header>

      {/* Watchlist panel */}
      {auth && showWatchlist && (
        <section className="panel">
          <p className="sectionLabel">My Watchlist</p>
          <WatchlistPanel
            items={watchlistItems}
            loading={watchlistLoading}
            onRemove={id => toggleWatchlist(id, '', '')}
          />
          <div className="sectionDivider" />
        </section>
      )}

      {/* Alerts panel */}
      {auth && showAlerts && (
        <section className="panel">
          <p className="sectionLabel">My Alerts</p>
          <AlertsPanel
            alerts={alerts}
            loading={alertsLoading}
            onDismiss={dismissAlert}
            onDelete={removeAlert}
          />
          <div className="sectionDivider" />
        </section>
      )}

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

      {isSearching && (
        <section>
          <p className="sectionLabel">
            {searchLoading ? 'Searching…' : `Results for "${search}"`}
          </p>
          {!searchLoading && searchResults.length === 0 && <p className="status">No items found.</p>}
          {!searchLoading && searchResults.length > 0 && (
            <FlipTable
              items={searchResults}
              selectedId={selected?.id ?? null}
              onSelect={handleSelectItem}
              watchlistIds={auth ? watchlistIds : undefined}
              onWatchlistToggle={auth ? item => toggleWatchlist(item.id, item.name, item.icon) : undefined}
              onCreateAlert={auth ? setAlertTarget : undefined}
            />
          )}
          <div className="sectionDivider" />
        </section>
      )}

      {isSearching && <p className="sectionLabel">Top {limit} flips</p>}
      {loading && <p className="status">Loading prices…</p>}
      {error   && <p className="status error">Error: {error}</p>}
      {!loading && !error && (
        <FlipTable
          items={topFlips}
          selectedId={selected?.id ?? null}
          onSelect={handleSelectItem}
          watchlistIds={auth ? watchlistIds : undefined}
          onWatchlistToggle={auth ? item => toggleWatchlist(item.id, item.name, item.icon) : undefined}
          onCreateAlert={auth ? setAlertTarget : undefined}
        />
      )}

      {alertTarget && (
        <CreateAlertModal
          item={alertTarget}
          onConfirm={margin => createAlert(alertTarget.id, alertTarget.name, alertTarget.icon, margin)}
          onClose={() => setAlertTarget(null)}
        />
      )}

      {showAuth && (
        <AuthForm onSuccess={handleAuthSuccess} onClose={() => setShowAuth(false)} />
      )}
    </div>
  );
}

export default App;
