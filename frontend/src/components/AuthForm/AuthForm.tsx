import { useState } from 'react';
import { loginUser, registerUser } from '../../api/auth';
import type { AuthResponse } from '../../types/auth';
import styles from './AuthForm.module.css';

interface Props {
  onSuccess: (auth: AuthResponse) => void;
  onClose: () => void;
}

type Tab = 'login' | 'register';

export function AuthForm({ onSuccess, onClose }: Props) {
  const [tab, setTab] = useState<Tab>('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const auth = tab === 'login'
        ? await loginUser(username, password)
        : await registerUser(username, email, password);
      onSuccess(auth);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <button className={styles.close} onClick={onClose}>✕</button>

        <div className={styles.tabs}>
          <button
            className={tab === 'login' ? styles.activeTab : styles.tab}
            onClick={() => { setTab('login'); setError(null); }}
          >
            Login
          </button>
          <button
            className={tab === 'register' ? styles.activeTab : styles.tab}
            onClick={() => { setTab('register'); setError(null); }}
          >
            Register
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <label className={styles.label}>
            Username
            <input
              className={styles.input}
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </label>

          {tab === 'register' && (
            <label className={styles.label}>
              Email
              <input
                className={styles.input}
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                autoComplete="email"
                required
              />
            </label>
          )}

          <label className={styles.label}>
            Password
            <input
              className={styles.input}
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              autoComplete={tab === 'login' ? 'current-password' : 'new-password'}
              minLength={tab === 'register' ? 8 : undefined}
              required
            />
          </label>

          {error && <p className={styles.error}>{error}</p>}

          <button className={styles.submit} type="submit" disabled={loading}>
            {loading ? 'Please wait…' : tab === 'login' ? 'Log in' : 'Create account'}
          </button>
        </form>
      </div>
    </div>
  );
}
