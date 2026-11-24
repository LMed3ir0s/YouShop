# domain/scenario/fixture — dados simulados (mock)

**Responsabilidade:** fornecer, para cada cenário, os dados fictícios que fazem o MVP funcionar sem integrações:

- eventos do **Sentry** (logs, incluindo dados sensíveis de propósito para testar o mascaramento);
- métricas do **Grafana**;
- tags do incidente;
- um **diagnóstico de referência**, usado pelo `MockLlmAdapter` no lugar de um LLM real.

| Arquivo                      | Cenário                                        |
|------------------------------|------------------------------------------------|
| `ScenarioFixture`            | Contrato de uma fixture                        |
| `FixtureCatalog`             | Reúne as fixtures e busca por tipo de cenário  |
| `CheckoutFailureFixture`     | 1 - Falha no Checkout                          |
| `FraudulentProductFixture`   | 2 - Fraude em Cadastro (inclui prompt injection)|
| `CreatorAttributionFixture`  | 3 - Atribuição a Creators                      |
| `CommissionPayoutFixture`    | 4 - Repasse de Comissões                       |

## Em produção

Esta pasta deixa de ser usada: a telemetria vem de adapters reais de Sentry/Grafana (`TelemetryPort`) e o
diagnóstico vem de um LLM real (`LlmPort`). Os cenários em `domain/scenario` continuam existindo.
