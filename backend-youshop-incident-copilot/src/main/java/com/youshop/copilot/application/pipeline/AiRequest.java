package com.youshop.copilot.application.pipeline;

import com.youshop.copilot.domain.model.HistoricalIncident;
import com.youshop.copilot.domain.model.ScenarioType;
import com.youshop.copilot.domain.model.TelemetrySnapshot;

import java.util.ArrayList;
import java.util.List;

/** Requisição imutável que atravessa a cadeia; cada middleware devolve uma cópia modificada. */
public record AiRequest(String incidentId, ScenarioType scenario, String systemPrompt, TelemetrySnapshot telemetry,
                        List<HistoricalIncident> fewShots, List<String> securityFlags) {
    public AiRequest {
        fewShots = List.copyOf(fewShots);
        securityFlags = List.copyOf(securityFlags);
    }

    public static AiRequest initial(String incidentId, ScenarioType scenario, String systemPrompt,
                                    TelemetrySnapshot telemetry) {
        return new AiRequest(incidentId, scenario, systemPrompt, telemetry, List.of(), List.of());
    }

    public AiRequest withTelemetry(TelemetrySnapshot t) {
        return new AiRequest(incidentId, scenario, systemPrompt, t, fewShots, securityFlags);
    }

    public AiRequest withSystemPrompt(String p) {
        return new AiRequest(incidentId, scenario, p, telemetry, fewShots, securityFlags);
    }

    public AiRequest withFewShots(List<HistoricalIncident> examples) {
        return new AiRequest(incidentId, scenario, systemPrompt, telemetry, examples, securityFlags);
    }

    public AiRequest withFlag(String flag) {
        var flags = new ArrayList<>(securityFlags);
        flags.add(flag);
        return new AiRequest(incidentId, scenario, systemPrompt, telemetry, fewShots, flags);
    }
}
