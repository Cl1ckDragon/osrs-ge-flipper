import { useEffect, useState } from 'react';
import { apiBase } from '../api/client';

export function useStats() {
  const [registeredUsers, setRegisteredUsers] = useState<number | null>(null);

  useEffect(() => {
    fetch(`${apiBase}/api/stats`)
      .then(r => r.json())
      .then(d => setRegisteredUsers((d as { registeredUsers: number }).registeredUsers))
      .catch(() => {});
  }, []);

  return registeredUsers;
}
