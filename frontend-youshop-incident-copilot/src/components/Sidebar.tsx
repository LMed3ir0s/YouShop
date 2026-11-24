import { Activity, ChevronLeft, ChevronRight, CircleAlert, ShieldAlert } from 'lucide-react';
import type { IncidentResponse } from '../types/incident';


interface Props {
    incidents: IncidentResponse[];
    selected: string;
    onSelect: (id: string) => void;
    collapsed: boolean;
    onToggle: () => void;
}

const severityStyle: Record<string, string> = { P0: 'bg-red-500/15 text-red-400 border-red-500/20', P1: 'bg-amber-500/15 text-amber-400 border-amber-500/20', P2: 'bg-blue-500/15 text-blue-400 border-blue-500/20' };

export default function Sidebar({ incidents, selected, onSelect, collapsed, onToggle }: Props) {
    return <aside className={`sidebar ${collapsed ? 'sidebar-collapsed' : ''}`}>
        <div className="sidebar-top"><div className="brand"><div className="brand-icon"><Activity size={19} /></div>{!collapsed && <div><div className="brand-name">YouShop <span>Ops</span></div><div className="brand-caption">INCIDENT COMMAND</div></div>}</div><button className="icon-button collapse" onClick={onToggle} aria-label="Recolher sidebar">{collapsed ? <ChevronRight size={16} /> : <ChevronLeft size={16} />}</button></div>
        {!collapsed && <><div className="nav-label">WORKSPACE</div><div className="nav-active"><Activity size={15} /> Incident Center <span className="nav-count">04</span></div><div className="nav-label incident-label">INCIDENTS <span className="live-dot" /></div></>}
        <div className="incident-list">{incidents.map(item => <button key={item.incidentId} onClick={() => onSelect(item.incidentId)} className={`incident-item ${selected === item.incidentId ? 'selected' : ''}`} title={item.scenario.name}>
            <span className={`severity ${severityStyle[item.scenario.severity]}`}>{item.scenario.severity}</span>{!collapsed && <span className="incident-text"><span className="incident-id">{item.incidentId}</span><span className="incident-name">{item.scenario.name}</span></span>}{collapsed && <span className="sr-only">{item.scenario.name}</span>}
        </button>)}</div>
        {!collapsed && <div className="sidebar-footer"><div className="footer-status"><span className="live-dot" /> All systems operational</div><div className="sidebar-footnote"><ShieldAlert size={13} /> Human approval enforced</div></div>}
    </aside>
}
