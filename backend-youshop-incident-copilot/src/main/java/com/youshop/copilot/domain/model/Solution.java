package com.youshop.copilot.domain.model;

import java.util.Map;

/** Solução proposta. {@code mcpTool} é opcional: ações sem ferramenta são executadas por humanos. */
public record Solution(String title, String description, RiskLevel risk, String mcpTool,
                       Map<String, Object> toolArguments, boolean requiresHumanApproval) {
    public Solution {
        toolArguments = toolArguments == null ? Map.of() : Map.copyOf(toolArguments);
    }

    public static Solution of(String title, String description, RiskLevel risk, String mcpTool,
                              Map<String, Object> args) {
        return new Solution(title, description, risk, mcpTool, args, risk != RiskLevel.BAIXO);
    }

    public Solution withoutTool() {
        return new Solution(title, description, risk, null, Map.of(), requiresHumanApproval);
    }
}
