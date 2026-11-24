package com.youshop.copilot.application.pipeline.middleware;

import com.youshop.copilot.application.pipeline.*;
import com.youshop.copilot.application.port.out.HistoricalIncidentPort;

import java.util.stream.Collectors;

/** Injeta 2 incidentes históricos resolvidos do mesmo tipo para guiar o formato e a profundidade do diagnóstico. */
public final class FewShotContextMiddleware implements AiMiddleware {

    private static final int EXAMPLES = 2;
    private final HistoricalIncidentPort history;

    public FewShotContextMiddleware(HistoricalIncidentPort history) {
        this.history = history;
    }

    @Override
    public AiResponse handle(AiRequest request, AiChain next) {
        var examples = history.findByScenario(request.scenario(), EXAMPLES);
        String block = examples.stream()
                .map(e -> "- [%s] Sintoma: %s | Causa raiz: %s | Resolução: %s (risco %s)"
                        .formatted(e.id(), e.symptom(), e.rootCause(), e.resolution(), e.resolutionRisk()))
                .collect(Collectors.joining("\n"));
        var prompt = request.systemPrompt() + "\n\nEXEMPLOS DE INCIDENTES HISTÓRICOS RESOLVIDOS:\n" + block;
        return next.proceed(request.withFewShots(examples).withSystemPrompt(prompt));
    }
}
