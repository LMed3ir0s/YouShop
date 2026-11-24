package com.youshop.copilot.application.pipeline;

import com.youshop.copilot.domain.model.Diagnosis;
import com.youshop.copilot.domain.model.TelemetrySnapshot;

import java.util.ArrayList;
import java.util.List;

public record AiResponse(Diagnosis diagnosis, TelemetrySnapshot telemetry, List<String> securityFlags) {
    public AiResponse {
        securityFlags = List.copyOf(securityFlags);
    }

    public AiResponse withDiagnosis(Diagnosis d) {
        return new AiResponse(d, telemetry, securityFlags);
    }

    public AiResponse withFlags(List<String> extra) {
        var all = new ArrayList<>(securityFlags);
        all.addAll(extra);
        return new AiResponse(diagnosis, telemetry, all);
    }
}
