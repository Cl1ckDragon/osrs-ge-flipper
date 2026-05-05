import { useState } from 'react';
import type { FlipOpportunity } from '../../types/prices';
import styles from './CreateAlertModal.module.css';

interface Props {
  item: FlipOpportunity;
  onConfirm: (targetMargin: number) => void;
  onClose: () => void;
}

export function CreateAlertModal({ item, onConfirm, onClose }: Props) {
  const [target, setTarget] = useState(Math.max(1, item.margin));

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    onConfirm(target);
    onClose();
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <button className={styles.close} onClick={onClose}>✕</button>

        <div className={styles.header}>
          <img
            src={`https://oldschool.runescape.wiki/images/${item.icon.replace(/ /g, '_')}`}
            alt="" width={24} height={24}
            style={{ imageRendering: 'pixelated' }}
          />
          <h2>{item.name}</h2>
        </div>

        <p className={styles.sub}>
          Current margin: <strong>{item.margin.toLocaleString()} gp</strong>
        </p>

        <form onSubmit={handleSubmit}>
          <label className={styles.label}>
            Alert me when margin reaches (gp)
            <input
              className={styles.input}
              type="number"
              min={1}
              value={target}
              onChange={e => setTarget(Math.max(1, Number(e.target.value)))}
              autoFocus
            />
          </label>
          <button className={styles.submit} type="submit">Set alert</button>
        </form>
      </div>
    </div>
  );
}
