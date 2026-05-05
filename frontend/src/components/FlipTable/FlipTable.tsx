import React from 'react';
import { HistoryChart } from '../HistoryChart/HistoryChart';
import type { FlipOpportunity } from '../../types/prices';
import styles from './FlipTable.module.css';

interface Props {
  items: FlipOpportunity[];
  selectedId: number | null;
  onSelect: (item: FlipOpportunity) => void;
}

export function FlipTable({ items, selectedId, onSelect }: Props) {
  if (items.length === 0) return <p className={styles.empty}>No items match the current filters.</p>;

  return (
    <table className={styles.table}>
      <thead>
        <tr>
          <th>Item</th>
          <th>Buy (low)</th>
          <th>Sell (high)</th>
          <th>Margin</th>
          <th>Buy Limit</th>
          <th>Flip Score</th>
          <th>Est. Profit</th>
        </tr>
      </thead>
      <tbody>
        {items.map(item => (
          <React.Fragment key={item.id}>
            <tr
              className={item.id === selectedId ? styles.selected : styles.row}
              onClick={() => onSelect(item)}
              title="Click to view margin history"
            >
              <td className={styles.name}>
                <img
                  src={`https://oldschool.runescape.wiki/images/${item.icon.replace(/ /g, '_')}`}
                  alt=""
                  className={styles.icon}
                  loading="lazy"
                  onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
                />
                {item.name}
              </td>
              <td>{item.low.toLocaleString()} gp</td>
              <td>{item.high.toLocaleString()} gp</td>
              <td className={item.margin >= 0 ? styles.positive : styles.negative}>
                {item.margin.toLocaleString()} gp
              </td>
              <td>{item.buyLimit.toLocaleString()}</td>
              <td>{item.flipScore.toLocaleString()}</td>
              <td className={styles.positive}>{item.potentialProfit.toLocaleString()} gp</td>
            </tr>

            {item.id === selectedId && (
              <tr className={styles.expandRow}>
                <td colSpan={7} className={styles.expandCell}>
                  <div className={styles.expandInner}>
                    <HistoryChart
                      itemId={item.id}
                      itemName={item.name}
                      onClose={() => onSelect(item)}
                    />
                  </div>
                </td>
              </tr>
            )}
          </React.Fragment>
        ))}
      </tbody>
    </table>
  );
}
