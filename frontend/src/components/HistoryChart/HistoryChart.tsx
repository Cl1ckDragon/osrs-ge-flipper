import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer,
} from 'recharts';
import { useHistory } from '../../hooks/useHistory';
import styles from './HistoryChart.module.css';

interface Props {
  itemId: number;
  itemName: string;
  onClose: () => void;
}

function formatTime(iso: string) {
  return new Date(iso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function formatGp(value: number) {
  return value >= 1_000_000
    ? `${(value / 1_000_000).toFixed(2)}m`
    : value >= 1_000
    ? `${(value / 1_000).toFixed(1)}k`
    : `${value}`;
}

export function HistoryChart({ itemId, itemName, onClose }: Props) {
  const { data, loading, error } = useHistory(itemId);

  return (
    <div className={styles.panel}>
      <div className={styles.header}>
        <h2 className={styles.title}>{itemName} — margin history (24h)</h2>
        <button className={styles.close} onClick={onClose}>✕</button>
      </div>

      {loading && <p className={styles.status}>Loading history…</p>}
      {error   && <p className={styles.status + ' ' + styles.error}>Error: {error}</p>}

      {!loading && !error && data.length === 0 && (
        <p className={styles.status}>
          No history yet — snapshots are taken every 5 minutes. Check back soon.
        </p>
      )}

      {!loading && !error && data.length > 0 && (
        <ResponsiveContainer width="100%" height={260}>
          <LineChart data={data} margin={{ top: 8, right: 24, left: 8, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#2a2a3e" />
            <XAxis
              dataKey="fetchedAt"
              tickFormatter={formatTime}
              tick={{ fill: '#888', fontSize: 11 }}
              minTickGap={40}
            />
            <YAxis
              tickFormatter={formatGp}
              tick={{ fill: '#888', fontSize: 11 }}
              width={52}
            />
            <Tooltip
              formatter={(value: unknown) => [`${Number(value).toLocaleString()} gp`, 'Margin']}
              labelFormatter={(label: unknown) => new Date(String(label)).toLocaleString()}
              contentStyle={{ background: '#1a1a2e', border: '1px solid #3a3a5e', borderRadius: 4 }}
              labelStyle={{ color: '#aaa' }}
            />
            <Line
              type="monotone"
              dataKey="margin"
              stroke="#f0c040"
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 4 }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
