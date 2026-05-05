import type { PriceAlert } from '../../types/alert';
import styles from './AlertsPanel.module.css';

interface Props {
  alerts: PriceAlert[];
  loading: boolean;
  onDismiss: (id: number) => void;
  onDelete: (id: number) => void;
}

export function AlertsPanel({ alerts, loading, onDismiss, onDelete }: Props) {
  if (loading) return <p className={styles.status}>Loading alerts…</p>;
  if (alerts.length === 0) return (
    <p className={styles.status}>
      No alerts yet — click 🔔 on any item to set a margin target.
    </p>
  );

  return (
    <table className={styles.table}>
      <thead>
        <tr>
          <th>Item</th>
          <th>Target Margin</th>
          <th>Current Margin</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        {alerts.map(alert => {
          const isNew = alert.triggered && !alert.dismissed;
          return (
            <tr key={alert.id} className={isNew ? styles.triggered : ''}>
              <td className={styles.name}>
                <img
                  src={`https://oldschool.runescape.wiki/images/${alert.icon.replace(/ /g, '_')}`}
                  alt="" width={20} height={20}
                  style={{ imageRendering: 'pixelated', flexShrink: 0 }}
                  onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
                />
                {alert.itemName}
              </td>
              <td>{alert.targetMargin.toLocaleString()} gp</td>
              <td className={alert.currentMargin != null && alert.currentMargin >= alert.targetMargin ? styles.positive : ''}>
                {alert.currentMargin != null ? `${alert.currentMargin.toLocaleString()} gp` : '—'}
              </td>
              <td>
                <div className={styles.statusCell}>
                  {isNew ? (
                    <span className={styles.badge}>Triggered ✓</span>
                  ) : alert.triggered ? (
                    <span className={styles.seen}>Seen</span>
                  ) : (
                    <span className={styles.watching}>Watching</span>
                  )}
                  <div className={styles.actions}>
                    {isNew && (
                      <button className={styles.dismissBtn} onClick={() => onDismiss(alert.id)} title="Dismiss">✓</button>
                    )}
                    <button className={styles.deleteBtn} onClick={() => onDelete(alert.id)} title="Delete">✕</button>
                  </div>
                </div>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
