package com.youshop.copilot.domain.model;

import java.time.Instant;

public record LogEntry(Instant timestamp, String level, String source, String message) {
    public static LogEntry sentry(Instant now, long secondsAgo, String level, String service, String message) {
        return new LogEntry(now.minusSeconds(secondsAgo), level, "sentry/" + service, message);
    }
}
