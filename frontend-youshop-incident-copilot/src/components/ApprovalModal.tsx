import { AlertTriangle, CheckCircle2, X } from 'lucide-react';
import type { IncidentResponse } from '../types/incident';


interface Props {
    incident: IncidentResponse;
    solution: IncidentResponse['analysis']['solutions'][number]; onClose: () => void;
    onApprove: () => void;
}

export default function ApprovalModal({ incident, solution, onClose, onApprove }: Props) {
    return <div className="modal-backdrop" role="presentation" onClick={onClose}><div className="modal" role="dialog" aria-modal="true" aria-labelledby="approval-title" onClick={e => e.stopPropagation()}>
        <div className="modal-head"><div className="modal-icon"><AlertTriangle size={19} /></div><div><h2 id="approval-title">Human approval required</h2><p>Revise a ação e confirme antes da execução.</p></div><button className="icon-button" onClick={onClose} aria-label="Fechar"><X size={17} /></button></div>
        <div className="modal-body"><div className="approval-summary"><span className="text-muted">INCIDENT</span><span className="mono text-lime">{incident.incidentId}</span><span className="text-muted">TOOL</span><span className="mono">{solution.mcpAction?.tool ?? 'tool não definida'}</span></div><h3>{solution.title}</h3><p className="modal-description">{solution.description}</p><div className="risk-row"><span className="text-muted">Risk level</span><span className={`risk-pill risk-${solution.risk.toLowerCase()}`}>{solution.risk}</span></div><div className="tool-payload"><div className="payload-title">REQUEST PAYLOAD <span>JSON</span></div><pre>{JSON.stringify(solution.mcpAction?.arguments ?? {}, null, 2)}</pre></div><div className="warning-box"><AlertTriangle size={15} /> Esta ação pode alterar sistemas operacionais. A confirmação neste MVP apenas simula o approval gate; nenhuma ferramenta real é executada.</div></div>
        <div className="modal-actions"><button className="btn-secondary" onClick={onClose}>Cancelar</button><button className="btn-primary" onClick={onApprove}><CheckCircle2 size={15} /> Confirmar aprovação simulada</button></div>
    </div></div>
}
