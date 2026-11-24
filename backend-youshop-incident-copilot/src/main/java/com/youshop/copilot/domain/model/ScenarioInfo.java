package com.youshop.copilot.domain.model;

public record ScenarioInfo(int id, String name, Severity severity, String description, String affectedService) {}
