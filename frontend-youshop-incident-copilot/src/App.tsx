
import { useState } from 'react';
import {
    ArrowDownRight,
    ArrowUpRight,
    Bot,
    Check,
    Clock3,
    Code2,
    Copy,
    MessageSquare,
    RefreshCw,
    Search,
    ShieldCheck,
    Sparkles,
    ThumbsDown,
    ThumbsUp,
    Zap,
} from 'lucide-react';

import Sidebar from './components/Sidebar';
import ApprovalModal from './components/ApprovalModal';

import { getIncident, type AppMode } from './services/api';
import { mockIncidents } from './data/mockIncidents';

import type { IncidentResponse } from './types/incident';

// -----------------------------------------------------------------------------
// Constants and presentation helpers
// -----------------------------------------------------------------------------

const DEFAULT_MODE: AppMode = 'API';

const levelColor = (level: string): string => {
    switch (level) {
        case 'ERROR':
            return 'log-error';
        case 'WARN':
            return 'log-warn';
        default:
            return 'log-info';
    }
};

const severityClass = (severity: string): string => {
    switch (severity) {
        case 'P0':
            return 'sev-p0';
        case 'P1':
            return 'sev-p1';
        default:
            return 'sev-p2';
    }
};

const riskClass = (risk: string): string => {
    switch (risk) {
        case 'BAIXO':
            return 'risk-baixo';
        case 'MEDIO':
            return 'risk-medio';
        default:
            return 'risk-alto';
    }
};

// -----------------------------------------------------------------------------
// Application
// -----------------------------------------------------------------------------

function App() {

    // Application state
    const [selected, setSelected] = useState('');
    const [searchId, setSearchId] = useState('');

    const [mode, setMode] = useState<AppMode>(() => {
        return import.meta.env.VITE_DEFAULT_MODE === 'MOCK'
            ? 'MOCK'
            : DEFAULT_MODE;
    });

    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    // Incident state
    const [incident, setIncident] = useState<IncidentResponse | null>(null);
    const [source, setSource] = useState<AppMode | null>(null);

    // Request state
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // Approval state
    const [modalSolution, setModalSolution] = useState<
        IncidentResponse['analysis']['solutions'][number] | null
    >(null);

    // Feedback state
    const [feedback, setFeedback] = useState<'up' | 'down' | null>(null);
    const [comment, setComment] = useState('');
    const [submitted, setSubmitted] = useState(false);

    // UI state
    const [copied, setCopied] = useState(false);

    // Histórico de incidentes consultados
    const [recentIncidents, setRecentIncidents] = useState<IncidentResponse[]>([]);

    // -------------------------------------------------------------------------
    // Incident operations
    // -------------------------------------------------------------------------

    async function loadIncident(
        id: string,
        requestedMode: AppMode = mode
    ): Promise<void> {
        const incidentId = id.trim();

        if (!incidentId) {
            setError('Informe um identificador de incidente.');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const result = await getIncident(incidentId, requestedMode);

            setIncident(result.data);
            setSource(result.source);
            setSelected(incidentId);
            setSearchId(incidentId);
            // Atualiza o histórico sem duplicar incidentes
            setRecentIncidents((current) => {
                const filtered = current.filter(
                    (item) => item.incidentId !== result.data.incidentId
                );

                return [result.data, ...filtered];
            });

            // Reset feedback when a new incident is loaded.
            setFeedback(null);
            setComment('');
            setSubmitted(false);
            setModalSolution(null);
        } catch (e) {
            setIncident(null);
            setSource(null);
            setSelected('');

            setError(
                e instanceof Error
                    ? e.message
                    : 'Falha inesperada ao consultar incidente.'
            );
        } finally {
            setLoading(false);
        }
    }

    function handleModeChange(nextMode: AppMode): void {
        if (nextMode === mode || loading) {
            return;
        }

        setMode(nextMode);

        // Reload the current incident using the selected data source.
        if (selected) {
            void loadIncident(selected, nextMode);
        }
    }

    function handleIncidentSelection(id: string): void {
        setSearchId(id);
        void loadIncident(id);
    }

    function handleRefresh(): void {
        if (selected) {
            void loadIncident(selected);
        }
    }

    // -------------------------------------------------------------------------
    // Payload and feedback operations
    // -------------------------------------------------------------------------

    async function copyPayload(): Promise<void> {
        if (!incident) {
            return;
        }

        const solution = incident.analysis.solutions[0];

        const payload = JSON.stringify(
            solution?.mcpAction?.arguments ?? {},
            null,
            2
        );

        try {
            await navigator.clipboard.writeText(payload);

            setCopied(true);

            window.setTimeout(() => {
                setCopied(false);
            }, 1500);
        } catch {
            setError('Não foi possível copiar o conteúdo para a área de transferência.');
        }
    }

    function submitFeedback(): void {
        if (!feedback) {
            return;
        }

        setSubmitted(true);
    }

    // -------------------------------------------------------------------------
    // Initial state: incident investigation
    // -------------------------------------------------------------------------

    if (!incident) {
        return (
            <div className="app-shell">
                <main className="main-area">
                    <header className="topbar">
                        <div className="topbar-title">
                            <div className="crumb">
                                OPS CENTER <span>/</span> INCIDENT INVESTIGATION
                            </div>

                            <h1>YouShop Incident Copilot</h1>
                        </div>

                        <div className="topbar-actions">
                            <div className="agent-status">
                                <span className="live-dot" />
                                <Bot size={15} />
                                <span>Agente MCP Ativo</span>
                            </div>

                            <div className="mode-switch">
                                <span className="mode-label">MODO</span>

                                <button
                                    className={
                                        mode === 'API'
                                            ? 'mode-option active'
                                            : 'mode-option'
                                    }
                                    onClick={() => handleModeChange('API')}
                                    disabled={loading}
                                >
                                    API Local
                                </button>

                                <button
                                    className={
                                        mode === 'MOCK'
                                            ? 'mode-option active'
                                            : 'mode-option'
                                    }
                                    onClick={() => handleModeChange('MOCK')}
                                    disabled={loading}
                                >
                                    Mock Vercel
                                </button>
                            </div>
                        </div>
                    </header>

                    <section
                        className="panel"
                        style={{
                            maxWidth: '600px',
                            margin: '80px auto',
                            padding: '32px',
                        }}
                    >
                        <div className="panel-heading">
                            <div className="heading-icon">
                                <Search size={16} />
                            </div>

                            <div>
                                <h2>Incident Investigation</h2>
                                <p>
                                    Enter an incident ID to start the analysis.
                                </p>
                            </div>
                        </div>

                        <form
                            onSubmit={(event) => {
                                event.preventDefault();
                                void loadIncident(searchId);
                            }}
                        >
                            <input
                                type="text"
                                value={searchId}
                                onChange={(event) => {
                                    setSearchId(event.target.value);
                                }}
                                placeholder="Ex.: INC-1001"
                                aria-label="Incident ID"
                                disabled={loading}
                                style={{
                                    width: '100%',
                                    marginTop: '24px',
                                    padding: '12px',
                                    borderRadius: '8px',
                                    color: 'Orange',
                                }}
                            />

                            <button
                                type="submit"
                                className="execute-btn"
                                disabled={loading || !searchId.trim()}
                                style={{
                                    width: '100%',
                                    marginTop: '12px',
                                }}
                            >
                                <Search size={15} />

                                {loading
                                    ? 'Investigating...'
                                    : 'Investigate Incident'}
                            </button>
                        </form>

                        {error && (
                            <div
                                className="error-banner"
                                style={{ marginTop: '16px' }}
                                role="alert"
                            >
                                {error}
                            </div>
                        )}

                        <p style={{ marginTop: '24px', opacity: 0.6 }}>
                            Data source: {mode === 'API' ? 'Local API' : 'Mock Data'}
                        </p>
                    </section>
                </main>
            </div>
        );
    }

    function handleNewInvestigation(): void {
        setIncident(null);
        setSelected('');
        setSearchId('');
        setSource(null);
        setError('');
        setFeedback(null);
        setComment('');
        setSubmitted(false);
        setModalSolution(null);
    }
    // From this point onward, an incident is guaranteed to be loaded.
    const solution = incident.analysis.solutions[0];

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    return (
        <div className="app-shell">
            <Sidebar
                incidents={recentIncidents}
                selected={selected}
                onSelect={handleIncidentSelection}
                collapsed={sidebarCollapsed}
                onToggle={() => setSidebarCollapsed(current => !current)}
            />

            <main className="main-area">
                {/* Header */}
                <header className="topbar">
                    <div className="topbar-title">
                        <div className="crumb">
                            OPS CENTER <span>/</span> INCIDENT ANALYSIS
                        </div>

                        <h1>YouShop Incident Copilot</h1>
                    </div>

                    <div className="topbar-actions">
                        <div className="agent-status">
                            <span className="live-dot" />
                            <Bot size={15} />
                            <span>Agente MCP Ativo</span>
                        </div>

                        <div className="mode-switch">
                            <span className="mode-label">MODO</span>

                            <button
                                className={
                                    mode === 'API'
                                        ? 'mode-option active'
                                        : 'mode-option'
                                }
                                onClick={() => handleModeChange('API')}
                                disabled={loading}
                            >
                                API Local
                            </button>

                            <button
                                className={
                                    mode === 'MOCK'
                                        ? 'mode-option active'
                                        : 'mode-option'
                                }
                                onClick={() => handleModeChange('MOCK')}
                                disabled={loading}
                            >
                                Mock Vercel
                            </button>
                        </div>

                        <button
                            className="execute-btn"
                            onClick={handleNewInvestigation}
                            disabled={loading}
                        >
                            <Search size={14} />
                            New Investigation
                        </button>

                        <button
                            className="icon-button"
                            onClick={handleRefresh}
                            title="Atualizar"
                            disabled={loading}
                        >
                            <RefreshCw
                                size={16}
                                className={loading ? 'spin' : ''}
                            />
                        </button>
                    </div>
                </header>

                {error && (
                    <div className="error-banner" role="alert">
                        {error}
                    </div>
                )}

                {/* Incident summary */}
                <section className="summary-strip">
                    <div className="summary-item">
                        <div className="summary-label">
                            <span className="live-dot" />
                            ACTIVE INCIDENT
                        </div>

                        <div className="summary-value mono">
                            {incident.incidentId}
                        </div>
                    </div>

                    <div className="summary-item">
                        <div className="summary-label">SEVERITY</div>

                        <div
                            className={`summary-value ${severityClass(
                                incident.scenario.severity
                            )}`}
                        >
                            <span
                                className={`severity ${severityClass(
                                    incident.scenario.severity
                                )}`}
                            >
                                {incident.scenario.severity}
                            </span>

                            <span>{incident.scenario.name}</span>
                        </div>
                    </div>

                    <div className="summary-item">
                        <div className="summary-label">AFFECTED SERVICE</div>

                        <div className="summary-value mono">
                            {incident.scenario.affectedService}
                        </div>
                    </div>

                    <div className="summary-item">
                        <div className="summary-label">COLLECTED AT</div>

                        <div className="summary-value">
                            <Clock3 size={14} />

                            {new Date(
                                incident.telemetry.collectedAt
                            ).toLocaleString('pt-BR', {
                                hour: '2-digit',
                                minute: '2-digit',
                                day: '2-digit',
                                month: 'short',
                            })}
                        </div>
                    </div>

                    <div className="summary-item source-item">
                        <div className="summary-label">DATA SOURCE</div>

                        <div className="summary-value">
                            <span
                                className={`source-badge ${source === 'API' ? 'api-source' : ''
                                    }`}
                            >
                                {source === 'API' ? 'LIVE API' : 'LOCAL MOCK'}
                            </span>
                        </div>
                    </div>
                </section>

                {/* Dashboard content */}
                <div className="dashboard-grid">
                    {/* Telemetry and logs */}
                    <section className="telemetry-panel panel">
                        <div className="panel-heading">
                            <div className="heading-icon">
                                <Code2 size={16} />
                            </div>

                            <div>
                                <h2>Telemetry & Logs</h2>
                                <p>Raw incident signals · Structured data</p>
                            </div>

                            <div className="panel-heading-right">
                                <span className="stream-tag">
                                    <span className="live-dot" />
                                    STREAM
                                </span>
                            </div>
                        </div>

                        <div className="telemetry-content">
                            {/* Event logs */}
                            <div className="subsection-head">
                                <span>EVENT LOGS</span>

                                <span className="log-count">
                                    {incident.telemetry.logs.length} events
                                </span>
                            </div>

                            <div className="logs-list">
                                {incident.telemetry.logs.map((log, index) => (
                                    <div
                                        className="log-row"
                                        key={`${log.timestamp}-${log.source}-${index}`}
                                    >
                                        <span className="log-time">
                                            {log.timestamp}
                                        </span>

                                        <span
                                            className={`log-level ${levelColor(
                                                log.level
                                            )}`}
                                        >
                                            {log.level}
                                        </span>

                                        <div className="log-details">
                                            <span className="log-source">
                                                {log.source}
                                            </span>

                                            <span className="log-message">
                                                {log.message}
                                            </span>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* Metrics */}
                            <div className="subsection-head metrics-head">
                                <span>METRICS SNAPSHOT</span>

                                <span className="log-count">
                                    {incident.telemetry.metrics.length} metrics
                                </span>
                            </div>

                            <div className="metrics-grid">
                                {incident.telemetry.metrics.map((metric) => (
                                    <div
                                        className="metric-card"
                                        key={metric.name}
                                    >
                                        <div className="metric-name">
                                            {metric.name}
                                        </div>

                                        <div className="metric-value">
                                            {metric.value.toLocaleString('pt-BR')}
                                            <span>{metric.unit}</span>
                                        </div>

                                        <div className="metric-source">
                                            SOURCE · {metric.source}
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* Context tags */}
                            <div className="subsection-head tags-head">
                                <span>CONTEXT TAGS</span>

                                <span className="log-count">
                                    {Object.keys(incident.telemetry.tags).length} tags
                                </span>
                            </div>

                            <div className="tags-grid">
                                {Object.entries(incident.telemetry.tags).map(
                                    ([key, value]) => (
                                        <div className="tag-cell" key={key}>
                                            <span>{key}</span>
                                            <strong>{value}</strong>
                                        </div>
                                    )
                                )}
                            </div>

                            {/* Security controls */}
                            <div className="subsection-head security-head">
                                <span>SECURITY CONTROLS</span>

                                <span className="security-verified">
                                    <ShieldCheck size={13} />
                                    ACTIVE
                                </span>
                            </div>

                            <div className="security-flags">
                                {incident.securityFlags.map((flag) => (
                                    <span className="security-chip" key={flag}>
                                        <Check size={12} />
                                        {flag}
                                    </span>
                                ))}
                            </div>

                            {/* Raw response */}
                            <div className="payload-section">
                                <div className="subsection-head">
                                    <span>RAW RESPONSE</span>

                                    <button
                                        className="copy-btn"
                                        onClick={() => void copyPayload()}
                                    >
                                        <Copy size={12} />
                                        {copied ? 'Copied' : 'Copy tool args'}
                                    </button>
                                </div>

                                <pre className="raw-json">
                                    {JSON.stringify(
                                        {
                                            incidentId: incident.incidentId,
                                            scenario: incident.scenario,
                                            telemetry: {
                                                collectedAt:
                                                    incident.telemetry.collectedAt,
                                                tags: incident.telemetry.tags,
                                            },
                                            securityFlags: incident.securityFlags,
                                        },
                                        null,
                                        2
                                    )}
                                </pre>
                            </div>
                        </div>
                    </section>

                    {/* Copilot diagnosis */}
                    <section className="copilot-panel panel">
                        <div className="panel-heading">
                            <div className="heading-icon copilot-icon">
                                <Sparkles size={16} />
                            </div>

                            <div>
                                <h2>Copilot Diagnosis</h2>
                                <p>AI-assisted analysis & recommendations</p>
                            </div>

                            <span className="confidence-badge">
                                <span className="live-dot" />
                                ANALYSIS READY
                            </span>
                        </div>

                        <div className="copilot-content">
                            {/* Observed symptom */}
                            <div className="diagnosis-block">
                                <div className="diagnosis-label">
                                    <span className="label-dot symptom-dot" />
                                    OBSERVED SYMPTOM
                                </div>

                                <p>{incident.analysis.symptom}</p>
                            </div>

                            {/* Root cause */}
                            <div className="diagnosis-block root-cause">
                                <div className="diagnosis-label">
                                    <span className="label-dot cause-dot" />
                                    ROOT CAUSE HYPOTHESIS
                                </div>

                                <p>{incident.analysis.rootCause}</p>
                            </div>

                            {/* Recommended solutions */}
                            <div className="solutions-heading">
                                <div>
                                    <h3>Recommended Actions</h3>
                                    <p>Ranked by operational risk</p>
                                </div>

                                <span className="solutions-count">
                                    {incident.analysis.solutions.length} OPTIONS
                                </span>
                            </div>

                            <div className="solutions-list">
                                {incident.analysis.solutions.map((item) => (
                                    <article
                                        className="solution-card"
                                        key={item.rank}
                                    >
                                        <div className="solution-top">
                                            <span className="rank">
                                                #{item.rank}
                                            </span>

                                            <span
                                                className={`risk-pill ${riskClass(
                                                    item.risk
                                                )}`}
                                            >
                                                {item.risk} RISK
                                            </span>
                                        </div>

                                        <h4>{item.title}</h4>
                                        <p>{item.description}</p>

                                        <div className="solution-meta">
                                            <span>
                                                <ShieldCheck size={12} />
                                                Human approval
                                            </span>

                                            <span className="tool-name">
                                                <Zap size={12} />
                                                {item.mcpAction?.tool}
                                            </span>
                                        </div>

                                        <button
                                            className="execute-btn"
                                            onClick={() => {
                                                setModalSolution(item);
                                            }}
                                        >
                                            <Zap size={14} />
                                            Execute MCP Tool
                                            <ArrowUpRight size={14} />
                                        </button>
                                    </article>
                                ))}
                            </div>

                            {/* Developer feedback */}
                            <div className="feedback-card">
                                <div className="feedback-title">
                                    <MessageSquare size={15} />

                                    <span>Developer Feedback</span>

                                    <span className="feedback-tag">
                                        IMPROVE AI
                                    </span>
                                </div>

                                {submitted ? (
                                    <div className="feedback-success">
                                        <Check size={15} />
                                        Feedback registrado neste protótipo.
                                        Obrigado!
                                    </div>
                                ) : (
                                    <>
                                        <p>
                                            Essa análise foi útil para resolver o
                                            incidente?
                                        </p>

                                        <div className="feedback-buttons">
                                            <button
                                                className={`feedback-choice ${feedback === 'up'
                                                    ? 'selected'
                                                    : ''
                                                    }`}
                                                onClick={() => {
                                                    setFeedback('up');
                                                }}
                                            >
                                                <ThumbsUp size={14} />
                                                Helpful
                                            </button>

                                            <button
                                                className={`feedback-choice ${feedback === 'down'
                                                    ? 'selected'
                                                    : ''
                                                    }`}
                                                onClick={() => {
                                                    setFeedback('down');
                                                }}
                                            >
                                                <ThumbsDown size={14} />
                                                Not helpful
                                            </button>
                                        </div>

                                        <textarea
                                            value={comment}
                                            onChange={(event) => {
                                                setComment(event.target.value);
                                            }}
                                            placeholder="Add context or suggest an improvement..."
                                            rows={2}
                                        />

                                        <button
                                            className="feedback-submit"
                                            disabled={!feedback}
                                            onClick={submitFeedback}
                                        >
                                            Submit feedback
                                            <ArrowDownRight size={13} />
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    </section>
                </div>

                {/* Disclaimer */}
                <footer className="disclaimer">
                    <div className="disclaimer-icon">⚠</div>

                    <div>
                        <strong>
                            AI-GENERATED RECOMMENDATIONS · HUMAN OVERSIGHT
                            REQUIRED
                        </strong>

                        <p>
                            Sugestão gerada por IA com validação de Prompt
                            Injection ativa. Aprovação humana necessária antes
                            do deploy.
                        </p>
                    </div>

                    <span className="disclaimer-state">
                        <ShieldCheck size={13} />
                        GUARDRAILS ON
                    </span>
                </footer>

                {/* Footer metadata */}
                <div className="bottom-meta">
                    <span>
                        <span className="live-dot" />
                        Incident monitoring enabled
                    </span>

                    <span>
                        UI MVP · v0.1.0
                        <span className="meta-sep"> | </span>
                        MCP actions are simulated
                    </span>
                </div>
            </main>

            {/* Human approval modal */}
            {modalSolution && (
                <ApprovalModal
                    incident={incident}
                    solution={modalSolution}
                    onClose={() => {
                        setModalSolution(null);
                    }}
                    onApprove={() => {
                        setModalSolution(null);
                        setSubmitted(false);
                        setFeedback(null);
                        setComment('');

                        window.alert(
                            'Approval simulation recorded. No MCP tool was executed.'
                        );
                    }}
                />
            )}
        </div>
    );
}

export default App;
