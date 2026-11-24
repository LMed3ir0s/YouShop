export type Severity = 'P0' | 'P1' | 'P2';
export type Risk = 'BAIXO' | 'MEDIO' | 'ALTO';
export interface IncidentResponse {
  incidentId: string;
  scenario: { id: number; name: string; severity: Severity; description: string; affectedService: string };
  telemetry: {
    collectedAt: string;
    tags: Record<string, string>;
    logs: Array<{ timestamp: string; level: string; source: string; message: string }>;
    metrics: Array<{ name: string; value: number; unit: string; source: string }>;
  };
  analysis: {
    symptom: string;
    rootCause: string;
    solutions: Array<{ rank: number; title: string; description: string; risk: Risk; requiresHumanApproval: boolean; mcpAction?: { tool: string; arguments: Record<string, any> } }>;
  };
  securityFlags: string[];
}
