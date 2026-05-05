import { useState } from 'react';
import { AuthForm } from './components/AuthForm/AuthForm';
import { FlipTable } from './components/FlipTable/FlipTable';
import { usePrices } from './hooks/usePrices';
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
  const [showAuth, setShowAuth]   = useState(false);
  const [auth, setAuth]           = useState<{ username: string } | null>(getSavedAuth);
  const [selected, setSelected]   = useState<FlipOpportunity | null>(null);

  const { data, loading, error } = usePrices(limit, minMargin);

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

      {loading && <p className="status">Loading prices…</p>}
      {error   && <p className="status error">Error: {error}</p>}
      {!loading && !error && (
        <FlipTable
          items={data}
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
