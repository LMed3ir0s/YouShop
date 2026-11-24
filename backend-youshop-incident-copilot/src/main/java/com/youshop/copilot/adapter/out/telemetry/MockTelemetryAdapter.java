package com.youshop.copilot.adapter.out.telemetry;

import com.youshop.copilot.application.port.out.TelemetryPort;
import com.youshop.copilot.domain.model.*;
import com.youshop.copilot.domain.scenario.ScenarioCatalog;
import com.youshop.copilot.domain.scenario.fixture.FixtureCatalog;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/** Simula Sentry (eventos/logs) e Grafana (métricas) a partir dos cenários e da configuração. */
@Component
public class MockTelemetryAdapter implements TelemetryPort {

    private static final String[] NOISE = {
            "GET /health 200 2ms",
            "Cache hit ratio 0.97 (catalog-cache)",
            "Job 'sitemap-refresh' concluído em 412ms",
            "Sessão s-8821 validada (ttl=3600s)",
            "Kafka consumer lag=3 partition=2 topic=orders.v1"};

    private final MockTelemetryProperties props;
    private final FixtureCatalog catalog;
    private final Map<String, ScenarioType> bindings;

    public MockTelemetryAdapter(MockTelemetryProperties props, FixtureCatalog catalog) {
        this.props = props;
        this.catalog = catalog;
        this.bindings = props.incidents().entrySet().stream().collect(Collectors.toUnmodifiableMap(
                e -> e.getKey().toLowerCase(Locale.ROOT), e -> ScenarioType.fromId(e.getValue())));
    }

    @Override
    public boolean exists(String incidentId) {
        return bindings.containsKey(incidentId.toLowerCase(Locale.ROOT));
    }

    @Override
    public Optional<TelemetrySnapshot> fetch(String incidentId) {
        var type = bindings.get(incidentId.toLowerCase(Locale.ROOT));
        if (type == null) return Optional.empty();
        simulateLatency();

        var fixture = catalog.get(type);
        var now = Instant.now();
        var logs = new ArrayList<LogEntry>();
        if (props.sentryEnabled()) logs.addAll(fixture.sentryEvents(now));
        logs.addAll(noise(incidentId, now));
        logs.sort(Comparator.comparing(LogEntry::timestamp));
        var metrics = props.grafanaEnabled() ? fixture.grafanaMetrics(now) : List.<MetricPoint>of();
        return Optional.of(new TelemetrySnapshot(incidentId, type, logs, metrics, fixture.tags(), now));
    }

    private List<LogEntry> noise(String incidentId, Instant now) {
        var random = new Random(props.seed() ^ incidentId.hashCode());
        var out = new ArrayList<LogEntry>();
        for (int i = 0; i < props.noiseLogLines(); i++) {
            out.add(new LogEntry(now.minusSeconds(random.nextInt(600)), "INFO", "sentry/breadcrumb",
                    NOISE[random.nextInt(NOISE.length)]));
        }
        return out;
    }

    private void simulateLatency() {
        long ms = props.simulatedLatency().toMillis();
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
