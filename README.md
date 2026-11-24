# YouShop Incident Copilot

> Microserviço e interface SPA que apoiam a Equipe de Desenvolvedores durante incidentes em produção, combinando telemetria sanitizada, diagnóstico por IA e ferramentas operacionais via protocolo **MCP (Model Context Protocol)** com controle humano (*Human-in-the-Loop*).

## Visão Geral

O **YouShop Incident Copilot** atua no momento crítico de um incidente:
1. **Coleta e Protege:** Recebe telemetria simulada (Sentry/Grafana), neutraliza tentativas de *prompt injection* e mascara dados sensíveis conforme a LGPD (CPF, e-mail, chaves de API).
2. **Diagnostica com IA:** Gera um relatório estruturado no padrão:
   **Sintoma → Causa Raiz → 3 Soluções recomendadas (Baixo, Médio e Alto risco)**.
3. **Propõe Ações MCP:** Expõe ações operacionais como ferramentas MCP (`restart_service`, `toggle_circuit_breaker`, `rerun_batch`).
4. **Human-in-the-Loop:** O copiloto apenas **propõe**. Ações de risco Médio/Alto exigem expressamente a revisão e aprovação humana antes de qualquer execução.

## Stack

| Camada | Tecnologias |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.5, Maven|
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS, Lucide React |
| **Padrões & Protocolos** | MCP (Model Context Protocol), RFC 9457 (Problem Details), REST |

## Rodando Localmente

### Backend

Abra um terminal e acesse a pasta do backend:

```powershell
cd backend

# Executar a aplicação (porta padrão: 8080)
.\mvnw spring-boot:run

# Rodar os testes automatizados
.\mvnw test

# Gerar o pacote JAR
.\mvnw clean package
java -jar target/incident-copilot-0.1.0-SNAPSHOT.jar
```


### Frontend

Em outro terminal, acesse a pasta do frontend:

```powershell
cd frontend

# Instalar dependências
npm install

# Iniciar o servidor de desenvolvimento
npm run dev
```

Acesse a interface no navegador em: **[http://localhost:5173](http://localhost:5173)**.


## Modos de Operação do Frontend (API vs Mock)

O frontend conta com uma chave de seleção no cabeçalho para alternar entre dois modos:
* **Modo Mock:** Utiliza dados estáticos pré-carregados (`src/data/mockIncidents.ts`), ideal para testes rápidos sem backend ativo.
* **Modo API Local:** Realiza chamadas HTTP para `http://localhost:8080/api/incidents/{id}`.

Para configurar a URL base da API, crie ou altere o arquivo `.env` na pasta `frontend/`:
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_DEFAULT_MODE=API # ou MOCK
```

## Cenários de Teste

No MVP, a telemetria é simulada. Utilize os identificadores abaixo para testar tanto na API quanto no frontend:

| ID | Cenário | Severidade | Resposta Esperada |
| :--- | :--- | :---: | :--- |
| `INC-1001` | Falha no Checkout (Gateway Timeout) | **P0** | Crítico: perda de receita; acionamento imediato |
| `INC-1002` | Fraude em Cadastro de Produto (Script) | **P1** | Alto: risco de segurança / injeção de prompt |
| `INC-1003` | Divergência de Atribuição a Creators | **P2** | Médio: impacto parcial; resolução planejada |
| `INC-1004` | Falha no Repasse de Comissões | **P0** | Crítico: impacto financeiro direto |

> 🔍 **Destaque de Segurança:** Teste o incidente `INC-1002` para verificar o pipeline de segurança neutralizando texto malicioso com a flag `[TRECHO_SUSPEITO_NEUTRALIZADO]` e mascarando dados sensíveis com `[CPF_MASKED]`, `[EMAIL_MASKED]` e `[API_KEY_MASKED]`.


## Endpoints Principais (Backend)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/scenarios` | Lista os 4 cenários disponíveis para análise |
| `GET` | `/api/incidents/{id}` | Retorna telemetria sanitizada + análise e soluções do copiloto |
| `POST`| `/api/incidents/{id}/feedback` | Registra a avaliação do dev (`👍`, `👎`, `UP`, `DOWN`) |
| `GET` | `/api/mcp/tools` | Lista as ferramentas disponíveis no formato MCP JSON Schema |

## Arquitetura do Backend

A requisição segue um fluxo desacoplado via Arquitetura Hexagonal:

```text
[IncidentController]
        │
        ▼
[IncidentCopilotService]
   ├───► [TelemetryPort]  (Coleta dados brutos simulados)
   └───► [AiPipeline]
             ├── 1. Masking (LGPD)
             ├── 2. Guard (Prompt Injection Defense)
             ├── 3. FewShot & Policy Rules
             └── 4. LLM Adapter (Gera diagnóstico e soluções)
```

## Limitações e Governança

* **Simulação de Componentes:** Telemetria (`MockTelemetryAdapter`) e LLM (`MockLlmAdapter`) utilizam adaptadores em memória para demonstração.
* **Persistência Volátil:** Feedbacks e estados ficam armazenados em memória (reiniciar o serviço limpa o histórico).
* **Execução MCP:** O copiloto apenas **propõe** as ferramentas e esquemas; a execução real em infraestrutura não ocorre de forma autônoma neste MVP.
* **Autenticação:** Endpoints abertos para fins de demonstração e testes locais.
