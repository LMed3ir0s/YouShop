package com.youshop.copilot.application.pipeline;

import com.youshop.copilot.application.port.out.LlmPort;

import java.util.List;

public final class AiPipeline {
    private final List<AiMiddleware> middlewares;
    private final LlmPort llm;

    public AiPipeline(List<AiMiddleware> middlewares, LlmPort llm) {
        this.middlewares = List.copyOf(middlewares);
        this.llm = llm;
    }

    public AiResponse execute(AiRequest request) {
        return chainFrom(0).proceed(request);
    }

    private AiChain chainFrom(int index) {
        if (index == middlewares.size()) {
            return req -> new AiResponse(llm.analyze(req), req.telemetry(), req.securityFlags());
        }
        return req -> middlewares.get(index).handle(req, chainFrom(index + 1));
    }
}
