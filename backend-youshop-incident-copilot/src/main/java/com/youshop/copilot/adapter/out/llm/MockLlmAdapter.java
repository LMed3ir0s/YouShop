package com.youshop.copilot.adapter.out.llm;

import com.youshop.copilot.application.pipeline.AiRequest;
import com.youshop.copilot.application.port.out.LlmPort;
import com.youshop.copilot.domain.model.Diagnosis;
import com.youshop.copilot.domain.scenario.fixture.FixtureCatalog;

import org.springframework.stereotype.Component;

/**
 * Motor determinístico para o MVP. Para um LLM real, troque este adapter por outro que renderize
 * {@code request.systemPrompt()} + a telemetria (já sanitizada) em um {@code <telemetry_data>} e use
 * saída estruturada (JSON) mapeada para {@link Diagnosis}.
 */
@Component
public class MockLlmAdapter implements LlmPort {

    private final FixtureCatalog catalog;

    public MockLlmAdapter(FixtureCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public Diagnosis analyze(AiRequest request) {
        return catalog.get(request.scenario()).cannedDiagnosis();
    }
}
