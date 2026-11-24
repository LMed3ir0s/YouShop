package com.youshop.copilot.domain.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public record TelemetrySnapshot(String incidentId, ScenarioType scenario, List<LogEntry> logs,
                                List<MetricPoint> metrics, Map<String, String> tags, Instant collectedAt) {
    public TelemetrySnapshot {
        logs = List.copyOf(logs);
        metrics = List.copyOf(metrics);
        tags = Map.copyOf(tags);
    }

    /** Aplica uma transformação de texto às mensagens de log e aos valores das tags (campos livres/não confiáveis). */
    public TelemetrySnapshot mapText(UnaryOperator<String> fn) {
        var newLogs = logs.stream()
                .map(l -> new LogEntry(l.timestamp(), l.level(), l.source(), fn.apply(l.message()))).toList();
        var newTags = new LinkedHashMap<String, String>();
        tags.forEach((k, v) -> newTags.put(k, fn.apply(v)));
        return new TelemetrySnapshot(incidentId, scenario, newLogs, metrics, newTags, collectedAt);
    }
}
