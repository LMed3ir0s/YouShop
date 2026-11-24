package com.youshop.copilot.application.port.out;

import com.youshop.copilot.application.pipeline.AiRequest;
import com.youshop.copilot.domain.model.Diagnosis;

/** Porta para o motor de diagnóstico (LLM). Sempre recebe uma requisição já passada pelo pipeline. */
public interface LlmPort {
    Diagnosis analyze(AiRequest request);
}
