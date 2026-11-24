package com.youshop.copilot.application.port.out;

import com.youshop.copilot.domain.model.TelemetrySnapshot;

import java.util.Optional;

/** Porta de saída agnóstica de fornecedor (Sentry, Grafana, Datadog, ...). Devolve telemetria BRUTA. */
public interface TelemetryPort {
    Optional<TelemetrySnapshot> fetch(String incidentId);
    boolean exists(String incidentId);
}
