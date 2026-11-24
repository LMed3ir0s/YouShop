package com.youshop.copilot.adapter.in.web;

import com.youshop.copilot.application.mcp.McpToolRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Equivalente a {@code tools/list} do MCP: lista as ferramentas que o agente pode propor. */
@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final McpToolRegistry registry;

    public McpController(McpToolRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/tools")
    public Map<String, Object> tools() {
        return Map.of("tools", registry.list());
    }
}
