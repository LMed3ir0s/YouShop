package com.youshop.copilot.application.mcp;

import java.util.Map;

/** Tool Definition no formato MCP (resposta de {@code tools/list}): name, title, description, inputSchema, annotations. */
public record McpToolDefinition(String name, String title, String description,
                                Map<String, Object> inputSchema, Annotations annotations) {

    public record Annotations(boolean readOnlyHint, boolean destructiveHint,
                              boolean idempotentHint, boolean openWorldHint) {}
}
