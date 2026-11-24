package com.youshop.copilot.domain.model;

import java.time.Instant;

public record MetricPoint(String name, double value, String unit, String source, Instant timestamp) {
    public static MetricPoint grafana(Instant now, String name, double value, String unit) {
        return new MetricPoint(name, value, unit, "grafana", now);
    }
}
