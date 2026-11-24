# application — o que o sistema faz

**Responsabilidade:** orquestrar os casos de uso. Conhece o `domain`, mas não sabe *como* falar com o mundo
externo: apenas declara o que precisa por meio de portas.

| Subpasta     | Conteúdo                                                                        |
|--------------|---------------------------------------------------------------------------------|
| `port/in/`   | O que o mundo pode **pedir** ao sistema (casos de uso)                          |
| `port/out/`  | O que o sistema **precisa** do mundo: telemetria, LLM, histórico, feedback      |
| `pipeline/`  | Esteira de IA: mascarar, neutralizar injeção, dar exemplos, impor política      |
| `mcp/`       | Catálogo das ferramentas que o agente pode propor (formato MCP)                 |
| `service/`   | `IncidentCopilotService`: o maestro que implementa os casos de uso              |

**Regra:** depende apenas de `domain`. Nunca importa nada de `adapter`.
