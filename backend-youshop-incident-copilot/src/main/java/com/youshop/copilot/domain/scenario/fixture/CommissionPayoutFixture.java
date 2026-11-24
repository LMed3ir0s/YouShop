package com.youshop.copilot.domain.scenario.fixture;

import com.youshop.copilot.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Fixture do cenário 4 — Falha no Repasse de Comissões (P0). */
public final class CommissionPayoutFixture implements ScenarioFixture {

    @Override public ScenarioType type() { return ScenarioType.COMMISSION_PAYOUT; }

    @Override
    public List<LogEntry> sentryEvents(Instant now) {
        return List.of(
            LogEntry.sentry(now, 1200, "INFO", "payout-worker", "Iniciando lote PAY-2026-11-W3 com 12480 transferências (chunk único)"),
            LogEntry.sentry(now, 900, "WARN", "payout-worker", "Lock em ledger_entries mantido por 640s pela tx 99231 (idempotency key payout:PAY-2026-11-W3:4212)"),
            LogEntry.sentry(now, 420, "ERROR", "payout-worker", "Transferência falhou para creator cpf=529.982.247-25 pix_key=lucas@creatormail.com: 504 Gateway Timeout"),
            LogEntry.sentry(now, 240, "ERROR", "payout-worker", "Lote PAY-2026-11-W3 excedeu 900s no item 4211/12480 (bank-api /transfers)"),
            LogEntry.sentry(now, 90, "DEBUG", "payout-worker", "Chamando banco com Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXlvdXQifQ.abc123def456ghi789"));
    }

    @Override
    public List<MetricPoint> grafanaMetrics(Instant now) {
        return List.of(
            MetricPoint.grafana(now, "payout_batch_progress", 33.7, "%"),
            MetricPoint.grafana(now, "bank_api_p99_latency", 48000, "ms"),
            MetricPoint.grafana(now, "ledger_lock_wait", 640, "s"),
            MetricPoint.grafana(now, "payout_pending_creators", 8269, "count"));
    }

    @Override
    public Map<String, String> tags() {
        return Map.of("service", "payout-worker", "env", "production",
                "batch", "PAY-2026-11-W3", "alert", "PayoutBatchTimeout");
    }

    @Override
    public Diagnosis cannedDiagnosis() {
        return new Diagnosis(
            "O lote PAY-2026-11-W3 parou em 33,7% (4.211 de 12.480 transferências) com timeouts; 8.269 creators seguem sem receber.",
            "O lote roda em chunk único com lock longo no ledger (640s); a lentidão do bank-api (p99 48s) estoura o timeout de 900s e bloqueia a retomada.",
            List.of(
                Solution.of("Simular a retomada do lote em chunks menores (dry-run)",
                    "Valida a retomada a partir do checkpoint 4211 em chunks de 500, sem efetuar transferências.",
                    RiskLevel.BAIXO, "rerun_batch",
                    Map.of("job", "commission-payout", "batch_id", "PAY-2026-11-W3", "dry_run", true, "chunk_size", 500)),
                Solution.of("Rolling restart do payout-worker para liberar o lock do ledger",
                    "Encerra a tx 99231 de forma graciosa e libera o lock, preservando as chaves de idempotência.",
                    RiskLevel.MEDIO, "restart_service",
                    Map.of("service", "payout-worker", "strategy", "rolling",
                           "reason", "Liberar lock longo em ledger_entries do lote PAY-2026-11-W3")),
                Solution.of("Retomar o lote de verdade em chunks de 500",
                    "Dispara as transferências restantes. Risco de pagamento duplicado se alguma idempotency key falhar; exige aprovação de Finance.",
                    RiskLevel.ALTO, "rerun_batch",
                    Map.of("job", "commission-payout", "batch_id", "PAY-2026-11-W3", "dry_run", false, "chunk_size", 500))));
    }
}
