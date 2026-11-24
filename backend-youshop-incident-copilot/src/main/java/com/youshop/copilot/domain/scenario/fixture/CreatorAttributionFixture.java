package com.youshop.copilot.domain.scenario.fixture;

import com.youshop.copilot.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Fixture do cenário 3 — Divergência de Atribuição de Vendas a Creators (P2). */
public final class CreatorAttributionFixture implements ScenarioFixture {

    @Override public ScenarioType type() { return ScenarioType.CREATOR_ATTRIBUTION; }

    @Override
    public List<LogEntry> sentryEvents(Instant now) {
        return List.of(
            LogEntry.sentry(now, 900, "INFO", "edge-config", "Deploy da edge config cfg-2026-11-19 aplicado em todas as regiões"),
            LogEntry.sentry(now, 600, "ERROR", "redirect-service", "302 de /r/abc descartou query string: header Location sem utm_* após cfg-2026-11-19"),
            LogEntry.sentry(now, 360, "WARN", "attribution-service", "utm_source=null em 23% dos pedidos originados do short link yshp.link/abc (creator=@lucas.creator, contato=contato@lucascreator.com)"),
            LogEntry.sentry(now, 120, "WARN", "payout-preview", "Prévia de comissões diverge: vendas_rastreadas=1842 vs vendas_gateway=2391 (delta -22,9%)"));
    }

    @Override
    public List<MetricPoint> grafanaMetrics(Instant now) {
        return List.of(
            MetricPoint.grafana(now, "attributed_orders_ratio", 77.1, "%"),
            MetricPoint.grafana(now, "orders_without_utm", 23.0, "%"),
            MetricPoint.grafana(now, "redirect_utm_drop_rate", 22.9, "%"),
            MetricPoint.grafana(now, "reconciliation_delta", -18432.55, "BRL"));
    }

    @Override
    public Map<String, String> tags() {
        return Map.of("service", "attribution-service", "env", "production",
                "config", "cfg-2026-11-19", "alert", "AttributionReconciliationDrift");
    }

    @Override
    public Diagnosis cannedDiagnosis() {
        return new Diagnosis(
            "Vendas atribuídas a creators estão 22,9% abaixo das vendas do gateway (delta de R$ 18.432,55), concentradas em short links /r/*.",
            "A edge config cfg-2026-11-19 passou a descartar a query string no redirect 302, removendo os parâmetros UTM antes do registro da atribuição.",
            List.of(
                Solution.of("Reconciliar atribuição em dry-run",
                    "Cruza pedidos do gateway com sessões/cookies (last-touch) para estimar as vendas perdidas, sem gravar nada.",
                    RiskLevel.BAIXO, "rerun_batch",
                    Map.of("job", "attribution-reconciliation", "batch_id", "recon-2026-11-19", "dry_run", true)),
                Solution.of("Reverter cfg-2026-11-19 e fazer rolling restart do redirect-service",
                    "Restaura o encaminhamento da query string e recarrega a configuração sem indisponibilidade total.",
                    RiskLevel.MEDIO, "restart_service",
                    Map.of("service", "redirect-service", "strategy", "rolling",
                           "reason", "Recarregar edge config após reversão da cfg-2026-11-19")),
                Solution.of("Reprocessar e gravar as atribuições retroativas",
                    "Reescreve atribuições desde 19/11, o que altera a base usada para o cálculo das comissões. Exige validação de Finance.",
                    RiskLevel.ALTO, "rerun_batch",
                    Map.of("job", "attribution-reconciliation", "batch_id", "recon-2026-11-19",
                           "dry_run", false, "chunk_size", 1000))));
    }
}
