package com.youshop.copilot.application.pipeline;

/** Próximo elo da cadeia (o último elo é a chamada ao LLM). */
@FunctionalInterface
public interface AiChain {
    AiResponse proceed(AiRequest request);
}
