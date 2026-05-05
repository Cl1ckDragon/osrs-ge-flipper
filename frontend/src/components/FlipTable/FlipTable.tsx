import type { FlipOpportunity } from '../../types/prices';
import styles from './FlipTable.module.css';

interface Props {
  items: FlipOpportunity[];
}

export function FlipTable({ items }: Props) {
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
          <tr key={item.id}>
            <td className={styles.name}>{item.name}</td>
            <td>{item.low.toLocaleString()} gp</td>
            <td>{item.high.toLocaleString()} gp</td>
            <td className={item.margin >= 0 ? styles.positive : styles.negative}>
              {item.margin.toLocaleString()} gp
            </td>
            <td>{item.buyLimit.toLocaleString()}</td>
            <td>{item.flipScore.toLocaleString()}</td>
            <td className={styles.positive}>{item.potentialProfit.toLocaleString()} gp</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
