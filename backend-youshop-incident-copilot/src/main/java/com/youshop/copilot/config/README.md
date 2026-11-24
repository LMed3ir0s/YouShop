# config — montagem do sistema

**Responsabilidade:** dizer ao Spring como ligar as peças.

`CopilotConfig` registra:
- o `ScenarioCatalog` com os 4 `Creator`;
- o `McpToolRegistry` com as 3 ferramentas;
- o `AiPipeline` com a **ordem** dos middlewares (mascarar → neutralizar → exemplos → política).

A ordem do pipeline é uma decisão de segurança: altere com cuidado.
