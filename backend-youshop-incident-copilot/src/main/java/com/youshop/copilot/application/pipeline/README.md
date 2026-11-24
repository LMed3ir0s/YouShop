# application/pipeline — esteira de IA (Chain of Responsibility)

**Responsabilidade:** tudo que acontece entre a telemetria bruta e o LLM, e entre a resposta do LLM e a API.

Ordem de execução (definida em `config/CopilotConfig`):

1. `DataMaskingMiddleware` — mascara CPF, e-mail e credenciais (LGPD). Vem primeiro: nenhum dado pessoal passa daqui.
2. `PromptInjectionGuardMiddleware` — trata logs como dado não confiável e neutraliza instruções maliciosas.
3. `FewShotContextMiddleware` — injeta 2 incidentes históricos resolvidos do mesmo tipo.
4. `TroubleshootingPolicyMiddleware` — impõe Sintoma → Causa Raiz → 3 soluções por risco e valida a saída.
5. `LlmPort` — último elo, chama o motor de diagnóstico.

| Arquivo         | Papel                                                              |
|-----------------|--------------------------------------------------------------------|
| `AiRequest`     | Pacote imutável que viaja pela esteira (cada elo devolve uma cópia)|
| `AiResponse`    | Pacote de volta (diagnóstico, telemetria final, avisos)            |
| `AiMiddleware`  | Contrato de um elo: age antes e/ou depois de chamar o próximo      |
| `AiChain`       | "Próximo elo"                                                      |
| `AiPipeline`    | Encadeia os elos na ordem recebida                                 |

**Para adicionar um middleware:** implemente `AiMiddleware` e inclua na lista de `CopilotConfig`. A ordem importa.
