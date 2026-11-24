package com.youshop.copilot.domain.scenario.fixture;

import com.youshop.copilot.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Dados simulados (mock) de um cenário: eventos do Sentry, métricas do Grafana e um diagnóstico de referência.
 * Existe só para o MVP funcionar sem integrações; em produção esses dados vêm de adapters reais.
 */
public interface ScenarioFixture {
    ScenarioType type();
    List<LogEntry> sentryEvents(Instant now);
    List<MetricPoint> grafanaMetrics(Instant now);
    Map<String, String> tags();
    /** Diagnóstico de referência, usado como linha de base pelo MockLlmAdapter. */
    Diagnosis cannedDiagnosis();
}
