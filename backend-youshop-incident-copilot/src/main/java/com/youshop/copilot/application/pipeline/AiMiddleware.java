package com.youshop.copilot.application.pipeline;

/**
 * Elo da Chain of Responsibility. Pode transformar a requisição antes de chamar {@code next.proceed(...)}
 * e/ou validar e corrigir a resposta depois (estilo around).
 */
public interface AiMiddleware {
    AiResponse handle(AiRequest request, AiChain next);
}
