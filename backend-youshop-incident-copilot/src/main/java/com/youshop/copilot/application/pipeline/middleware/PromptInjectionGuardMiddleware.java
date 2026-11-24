package com.youshop.copilot.application.pipeline.middleware;

import com.youshop.copilot.application.pipeline.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Trata logs e tags como DADO NÃO CONFIÁVEL: neutraliza instruções para o modelo, tentativas de invocar
 * ferramentas MCP, comandos destrutivos, marcação ativa (script/iframe) e spoof de delimitadores.
 */
public final class PromptInjectionGuardMiddleware implements AiMiddleware {

    static final String REPLACEMENT = "[TRECHO_SUSPEITO_NEUTRALIZADO]";

    private static final List<Pattern> PATTERNS = Stream.of(
            "ignore\\s+(?:all\\s+|the\\s+)*(?:previous|prior|above|earlier)\\s+(?:instructions?|prompts?|rules)",
            "(?:desconsidere|ignore|esque[cç]a)\\s+(?:todas\\s+)?(?:as\\s+)?(?:instru[cç][õo]es|regras|ordens)(?:\\s+(?:anteriores|acima))?",
            "(?:you\\s+are\\s+now|voc[eê]\\s+agora\\s+[eé]|finja\\s+ser)\\b[^.\\n]*",
            "(?:system|developer)\\s*prompt",
            "</?\\s*(?:script|iframe|object|embed|style)\\b[^>]*>",
            "\\b(?:call|invoke|execute|run|chame)\\s+(?:the\\s+)?(?:tool\\s+)?(?:restart_service|toggle_circuit_breaker|rerun_batch)\\b[^.\\n']*",
            "\\b(?:rm\\s+-rf|drop\\s+table|truncate\\s+table)\\b[^.\\n]*",
            "</?\\s*telemetry_data\\s*>|<\\|[a-z_]+\\|>|\\[/?INST\\]"
    ).map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).toList();

    @Override
    public AiResponse handle(AiRequest request, AiChain next) {
        var hits = new AtomicInteger();
        var sanitized = request.telemetry().mapText(text -> neutralize(text, hits));
        var updated = request.withTelemetry(sanitized);
        if (hits.get() > 0) {
            updated = updated.withFlag("PROMPT_INJECTION: %d padrão(ões) malicioso(s) neutralizado(s) na telemetria"
                    .formatted(hits.get()));
        }
        return next.proceed(updated);
    }

    private static String neutralize(String text, AtomicInteger hits) {
        if (text == null || text.isEmpty()) return text;
        String out = text;
        for (Pattern p : PATTERNS) {
            var m = p.matcher(out);
            int before = hits.get();
            while (m.find()) hits.incrementAndGet();
            if (hits.get() > before) out = p.matcher(out).replaceAll(REPLACEMENT);
        }
        return out;
    }
}
