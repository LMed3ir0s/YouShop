package com.youshop.copilot.domain.scenario.fixture;

import com.youshop.copilot.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Fixture do cenário 1 — Falha no Checkout (P0). */
public final class CheckoutFailureFixture implements ScenarioFixture {

    @Override public ScenarioType type() { return ScenarioType.CHECKOUT_FAILURE; }

    @Override
    public List<LogEntry> sentryEvents(Instant now) {
        return List.of(
            LogEntry.sentry(now, 540, "WARN", "checkout-service", "Latência elevada no PSP: POST /v1/charges p99=8200ms (limite=2000ms) order=ORD-88190"),
            LogEntry.sentry(now, 420, "ERROR", "payment-gateway", "GatewayTimeoutException: POST https://psp.example/v1/charges excedeu 30000ms order=ORD-88231 customer=maria.silva@email.com cpf=123.456.789-09"),
            LogEntry.sentry(now, 300, "WARN", "checkout-service", "Retry 3/3 sem backoff para o PSP; fila de retries=1840"),
            LogEntry.sentry(now, 240, "ERROR", "checkout-service", "HikariPool-1 - Connection is not available, request timed out after 30000ms (active=50, pending=212)"),
            LogEntry.sentry(now, 180, "INFO", "checkout-service", "Circuit breaker 'psp-gateway' state=CLOSED failureRate=48% threshold=50% (próximo de abrir)"),
            LogEntry.sentry(now, 60, "ERROR", "api-gateway", "504 Gateway Timeout em POST /api/checkout (38,4% das requisições nos últimos 5min)"),
            LogEntry.sentry(now, 30, "DEBUG", "checkout-service", "Credenciais do PSP carregadas: api_key=sk_live_51Hx9aQ2eZvKYlo2C0abcDEF123456"));
    }

    @Override
    public List<MetricPoint> grafanaMetrics(Instant now) {
        return List.of(
            MetricPoint.grafana(now, "checkout_success_rate", 61.2, "%"),
            MetricPoint.grafana(now, "http_5xx_rate", 38.4, "%"),
            MetricPoint.grafana(now, "psp_gateway_p99_latency", 31200, "ms"),
            MetricPoint.grafana(now, "hikari_pending_connections", 212, "count"),
            MetricPoint.grafana(now, "orders_per_minute", 42, "orders/min"));
    }

    @Override
    public Map<String, String> tags() {
        return Map.of("service", "checkout-service", "env", "production",
                "release", "2026.11.20-3", "alert", "CheckoutSuccessRateLow");
    }

    @Override
    public Diagnosis cannedDiagnosis() {
        return new Diagnosis(
            "Taxa de sucesso do checkout caiu para 61,2% (normal ~98%), com 38,4% de 5xx, p99 do PSP em 31s e vazão de 42 pedidos/min (normal ~310).",
            "Lentidão no PSP combinada com retries sem backoff esgotou o pool Hikari (212 conexões pendentes), gerando falha em cascata no checkout.",
            List.of(
                Solution.of("Abrir o circuit breaker do PSP",
                    "Falha rápida nas chamadas ao PSP, libera threads/conexões e permite fallback de pagamento (ex.: Pix).",
                    RiskLevel.BAIXO, "toggle_circuit_breaker",
                    Map.of("service", "checkout-service", "breaker", "psp-gateway", "state", "OPEN")),
                Solution.of("Rolling restart do checkout-service",
                    "Recicla o pool de conexões esgotado sem derrubar todas as instâncias ao mesmo tempo.",
                    RiskLevel.MEDIO, "restart_service",
                    Map.of("service", "checkout-service", "strategy", "rolling",
                           "reason", "Liberar pool Hikari esgotado durante incidente INC do checkout")),
                Solution.of("Failover manual para PSP secundário e rollback do release 2026.11.20-3",
                    "Redireciona o tráfego de pagamento para o PSP secundário e reverte o release. Exige coordenação com Payments.",
                    RiskLevel.ALTO, null, Map.of())));
    }
}
