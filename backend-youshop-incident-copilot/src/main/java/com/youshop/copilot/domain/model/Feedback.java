package com.youshop.copilot.domain.model;

import java.time.Instant;

public record Feedback(String incidentId, Rating rating, String comment, Instant createdAt) {}
