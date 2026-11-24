# adapter — a ponte para o mundo real

**Responsabilidade:** todo detalhe técnico (HTTP, JSON, fornecedores, armazenamento). Cada adapter se conecta a uma
porta definida em `application/port`. É a camada mais trocável do sistema.

| Pasta           | Direção                       | Conteúdo                                                |
|-----------------|-------------------------------|---------------------------------------------------------|
| `in/web/`       | Mundo → sistema               | Controllers REST, DTOs (`ApiDtos`), tratamento de erros |
| `out/telemetry/`| Sistema → mundo               | Telemetria simulada (Sentry + Grafana)                  |
| `out/llm/`      | Sistema → mundo               | Motor de diagnóstico simulado                           |
| `out/history/`  | Sistema → mundo               | Incidentes históricos (em memória)                      |
| `out/feedback/` | Sistema → mundo               | Armazenamento do feedback (em memória)                  |

## O que trocar para sair do MVP

| Hoje                              | Produção                                          | Porta implementada     |
|-----------------------------------|---------------------------------------------------|------------------------|
| `MockTelemetryAdapter`            | Cliente real Sentry/Grafana                       | `TelemetryPort`        |
| `MockLlmAdapter`                  | Cliente de LLM com saída estruturada              | `LlmPort`              |
| `InMemoryHistoricalIncidentAdapter` | Base de incidentes passados (banco/busca)       | `HistoricalIncidentPort` |
| `InMemoryFeedbackAdapter`         | Banco de dados                                    | `FeedbackPort`         |

Trocar uma tecnologia deve exigir mudar **apenas o adapter** (e, no máximo, a configuração).
