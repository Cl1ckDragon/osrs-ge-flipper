import type { WatchlistItem } from '../../types/watchlist';
import styles from './WatchlistPanel.module.css';

interface Props {
  items: WatchlistItem[];
  loading: boolean;
  onRemove: (itemId: number) => void;
}

export function WatchlistPanel({ items, loading, onRemove }: Props) {
  if (loading) return <p className={styles.status}>Loading watchlist…</p>;
  if (items.length === 0) return (
    <p className={styles.status}>
      Your watchlist is empty — click ⭐ on any item to save it.
    </p>
  );

  return (
    <table className={styles.table}>
      <thead>
        <tr>
          <th>Item</th>
          <th>Buy (low)</th>
          <th>Sell (high)</th>
          <th>Margin</th>
          <th>Flip Score</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {items.map(item => (
          <tr key={item.itemId}>
            <td className={styles.name}>
              <img
                src={`https://oldschool.runescape.wiki/images/${item.icon.replace(/ /g, '_')}`}
                alt="" width={20} height={20}
                style={{ imageRendering: 'pixelated', flexShrink: 0 }}
                onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
              />
              {item.itemName}
            </td>
            <td>{item.low != null ? `${item.low.toLocaleString()} gp` : '—'}</td>
            <td>{item.high != null ? `${item.high.toLocaleString()} gp` : '—'}</td>
            <td className={item.margin != null && item.margin >= 0 ? styles.positive : styles.negative}>
              {item.margin != null ? `${item.margin.toLocaleString()} gp` : '—'}
            </td>
            <td>{item.flipScore != null ? item.flipScore.toLocaleString() : '—'}</td>
            <td>
              <button className={styles.removeBtn} onClick={() => onRemove(item.itemId)} title="Remove">✕</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
