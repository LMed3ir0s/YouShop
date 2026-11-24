
import { mockIncidents } from '../data/mockIncidents';
import type { IncidentResponse } from '../types/incident';

export type AppMode = 'API' | 'MOCK';

const API_BASE = (
  import.meta.env.VITE_API_BASE_URL as string | undefined
)?.replace(/\/$/, '') || 'http://localhost:8080';

export async function getIncident(id: string, mode: AppMode): Promise<{ data: IncidentResponse; source: AppMode }> {

  if (mode === 'API') {
    const response = await fetch(
      `${API_BASE}/api/incidents/${encodeURIComponent(id)}`,
      {
        headers: {
          Accept: 'application/json'
        }
      }
    );

    if (!response.ok) {
      throw new Error(`API respondeu HTTP ${response.status}`);
    }

    const data = await response.json() as IncidentResponse;

    return { data, source: 'API' };
  }

  const mock = mockIncidents.find(
    item => item.incidentId === id
  );

  if (!mock) {
    throw new Error(`Incidente ${id} não existe no mock local.`);
  }

  return {
    data: structuredClone(mock),
    source: 'MOCK'
  };
}

export const incidentIds = mockIncidents.map(
  item => item.incidentId
);
