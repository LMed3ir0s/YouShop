package com.youshop.copilot.domain.model;

public record HistoricalIncident(String id, ScenarioType scenario, String symptom, String rootCause,
                                 String resolution, RiskLevel resolutionRisk) {}
