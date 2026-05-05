import { useState } from 'react';
import { FlipTable } from './components/FlipTable/FlipTable';
import { usePrices } from './hooks/usePrices';
import './App.css';

function App() {
  const [limit, setLimit] = useState(50);
  const [minMargin, setMinMargin] = useState(100);
  const { data, loading, error } = usePrices(limit, minMargin);

  return (
    <div className="container">
      <header>
        <h1>OSRS GE Flipper</h1>
        <p className="subtitle">Live Grand Exchange flip opportunities, sorted by score</p>
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

      {loading && <p className="status">Loading prices...</p>}
      {error && <p className="status error">Error: {error}</p>}
      {!loading && !error && <FlipTable items={data} />}
    </div>
  );
}

export default App;
