# domain/scenario — os cenários de incidente

**Responsabilidade:** representar cada tipo de incidente (checkout, fraude, atribuição, repasse) e criá-los via
**Factory Method**. Aqui ficam só as **definições**: tipo, nome, severidade, descrição e serviço afetado.

| Arquivo                  | Papel                                                          |
|--------------------------|----------------------------------------------------------------|
| `IncidentScenario`       | Contrato de um cenário (`type()` e `info()`)                   |
| `ScenarioCreator`        | Fábrica abstrata; cada cenário tem um `Creator` aninhado       |
| `ScenarioCatalog`        | Reúne os cenários criados e busca por tipo                     |
| `*Scenario` (4 classes)  | Um arquivo por cenário, apenas com a sua definição             |
| `fixture/`               | Dados simulados (mock) de cada cenário. Ver `fixture/README.md`|

## Como adicionar um cenário

1. Adicione o valor em `ScenarioType`.
2. Crie a classe do cenário com seu `Creator` aninhado.
3. Crie a fixture correspondente em `fixture/` e registre-a em `FixtureCatalog`.
4. Registre o `Creator` em `config/CopilotConfig`.
5. Mapeie um ID de incidente para ele em `application.yml`.
6. (Opcional) Adicione exemplos em `InMemoryHistoricalIncidentAdapter`.
