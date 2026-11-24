package com.youshop.copilot.application.pipeline.middleware;

import com.youshop.copilot.application.pipeline.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** LGPD: mascara CPFs (com validação de dígitos), e-mails e credenciais antes de qualquer envio ao LLM. */
public final class DataMaskingMiddleware implements AiMiddleware {

    private static final Pattern SECRET_ASSIGNMENT = Pattern.compile(
            "(?i)\\b(api[_-]?key|secret|token|password|authorization)\\s*[=:]\\s*(?:Bearer\\s+)?['\"]?[^\\s'\",;]+");
    private static final Pattern VENDOR_KEY = Pattern.compile("\\b(?:(?:sk|pk|rk)_(?:live|test)_[A-Za-z0-9]{8,}|AKIA[0-9A-Z]{16})\\b");
    private static final Pattern JWT = Pattern.compile("\\beyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]*");
    private static final Pattern BEARER = Pattern.compile("(?i)\\bBearer\\s+[A-Za-z0-9._~+/=-]{16,}");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern CPF = Pattern.compile("(?<!\\d)\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}(?!\\d)");

    @Override
    public AiResponse handle(AiRequest request, AiChain next) {
        var changed = new AtomicInteger();
        var masked = request.telemetry().mapText(text -> {
            String out = mask(text);
            if (!out.equals(text)) changed.incrementAndGet();
            return out;
        });
        var updated = request.withTelemetry(masked);
        if (changed.get() > 0) {
            updated = updated.withFlag("LGPD_MASKING: %d campo(s) com dados sensíveis mascarados".formatted(changed.get()));
        }
        return next.proceed(updated);
    }

    static String mask(String text) {
        if (text == null || text.isEmpty()) return text;
        String out = SECRET_ASSIGNMENT.matcher(text).replaceAll("$1=[API_KEY_MASKED]");
        out = VENDOR_KEY.matcher(out).replaceAll("[API_KEY_MASKED]");
        out = JWT.matcher(out).replaceAll("[API_KEY_MASKED]");
        out = BEARER.matcher(out).replaceAll("Bearer [API_KEY_MASKED]");
        out = EMAIL.matcher(out).replaceAll("[EMAIL_MASKED]");
        return CPF.matcher(out).replaceAll((MatchResult mr) -> validCpf(mr.group().replaceAll("\\D", ""))
                ? "[CPF_MASKED]" : Matcher.quoteReplacement(mr.group()));
    }

    private static boolean validCpf(String d) {
        if (d.length() != 11 || d.chars().distinct().count() == 1) return false;
        return checkDigit(d, 9) == d.charAt(9) - '0' && checkDigit(d, 10) == d.charAt(10) - '0';
    }

    private static int checkDigit(String d, int len) {
        int sum = 0;
        for (int i = 0; i < len; i++) sum += (d.charAt(i) - '0') * (len + 1 - i);
        int r = (sum * 10) % 11;
        return r == 10 ? 0 : r;
    }
}
