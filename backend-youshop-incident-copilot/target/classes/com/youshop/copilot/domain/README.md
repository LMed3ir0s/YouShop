# domain — o vocabulário do negócio

**Responsabilidade:** definir os conceitos do problema (incidente, telemetria, diagnóstico, solução, risco) e as
regras puras sobre eles.

**Regra de ouro:** sem Spring, sem HTTP, sem banco. O `domain` não importa `application` nem `adapter`.

| Subpasta     | Conteúdo                                                                  |
|--------------|---------------------------------------------------------------------------|
| `model/`     | Substantivos do negócio (`Diagnosis`, `Solution`, `TelemetrySnapshot`...) |
| `scenario/`  | Os 4 cenários de incidente da YouShop (ver README da pasta)               |
| `exception/` | Erros do negócio (ex.: incidente não encontrado)                          |

**Quando mexer aqui:** surgiu um conceito ou regra de negócio novo.
