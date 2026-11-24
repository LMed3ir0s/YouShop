package com.youshop.copilot.domain.model;

import java.time.Instant;
import java.util.List;

public record IncidentAnalysis(String incidentId, ScenarioInfo scenario, TelemetrySnapshot sanitizedTelemetry,
                               Diagnosis diagnosis, List<String> securityFlags, Instant analyzedAt) {}
