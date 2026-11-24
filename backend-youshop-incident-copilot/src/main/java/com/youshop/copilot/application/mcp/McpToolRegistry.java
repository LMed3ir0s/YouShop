package com.youshop.copilot.application.mcp;

import com.youshop.copilot.application.mcp.McpToolDefinition.Annotations;

import java.util.*;

/**
 * Ações que o agente PODE PROPOR. O copiloto apenas declara as ferramentas; a execução fica fora do MVP
 * e passa por aprovação humana para riscos MEDIO/ALTO.
 */
public final class McpToolRegistry {

    private final Map<String, McpToolDefinition> tools = new LinkedHashMap<>();

    public McpToolRegistry() {
        List.of(restartService(), toggleCircuitBreaker(), rerunBatch()).forEach(t -> tools.put(t.name(), t));
    }

    public List<McpToolDefinition> list() { return List.copyOf(tools.values()); }
    public Set<String> names() { return Collections.unmodifiableSet(tools.keySet()); }
    public boolean contains(String name) { return tools.containsKey(name); }

    private static McpToolDefinition restartService() {
        return new McpToolDefinition("restart_service", "Reiniciar serviço",
            "Reinicia um serviço da YouShop. Use 'rolling' para reciclar instância por instância sem indisponibilidade total.",
            schema(Map.of(
                "service", prop("string", "Nome do serviço (ex.: checkout-service)"),
                "strategy", Map.of("type", "string", "description", "Estratégia de reinício", "enum", List.of("rolling", "immediate")),
                "reason", prop("string", "Justificativa registrada na trilha de auditoria")),
                List.of("service", "strategy")),
            new Annotations(false, true, false, false));
    }

    private static McpToolDefinition toggleCircuitBreaker() {
        return new McpToolDefinition("toggle_circuit_breaker", "Alternar circuit breaker",
            "Abre ou fecha um circuit breaker de uma dependência para falhar rápido ou restabelecer o tráfego.",
            schema(Map.of(
                "service", prop("string", "Serviço dono do breaker"),
                "breaker", prop("string", "Nome do breaker (ex.: psp-gateway)"),
                "state", Map.of("type", "string", "description", "Estado alvo", "enum", List.of("OPEN", "HALF_OPEN", "CLOSED"))),
                List.of("service", "breaker", "state")),
            new Annotations(false, false, true, false));
    }

    private static McpToolDefinition rerunBatch() {
        return new McpToolDefinition("rerun_batch", "Reexecutar lote",
            "Reexecuta um job em lote. Com dry_run=true apenas simula, sem efeitos colaterais.",
            schema(Map.of(
                "job", prop("string", "Nome do job (ex.: commission-payout)"),
                "batch_id", prop("string", "Identificador do lote"),
                "dry_run", prop("boolean", "Se true, simula sem gravar nem transferir"),
                "chunk_size", Map.of("type", "integer", "description", "Itens por chunk", "minimum", 1)),
                List.of("job", "batch_id", "dry_run")),
            new Annotations(false, true, true, false));
    }

    private static Map<String, Object> prop(String type, String description) {
        return Map.of("type", type, "description", description);
    }

    private static Map<String, Object> schema(Map<String, Object> properties, List<String> required) {
        return Map.of("type", "object", "properties", properties, "required", required, "additionalProperties", false);
    }
}
