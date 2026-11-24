package com.youshop.copilot.adapter.out.telemetry;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.Map;

/** Telemetria simulada configurável via application.yml (prefixo youshop.telemetry.mock). */
@ConfigurationProperties(prefix = "youshop.telemetry.mock")
public record MockTelemetryProperties(
        Map<String, Integer> incidents,
        @DefaultValue("true") boolean sentryEnabled,
        @DefaultValue("true") boolean grafanaEnabled,
        @DefaultValue("3") int noiseLogLines,
        @DefaultValue("0ms") Duration simulatedLatency,
        @DefaultValue("42") long seed) {

    public MockTelemetryProperties {
        incidents = incidents == null ? Map.of() : Map.copyOf(incidents);
    }
}
