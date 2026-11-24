package com.youshop.copilot.adapter.out.history;

import com.youshop.copilot.application.port.out.HistoricalIncidentPort;
import com.youshop.copilot.domain.model.HistoricalIncident;
import com.youshop.copilot.domain.model.RiskLevel;
import com.youshop.copilot.domain.model.ScenarioType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InMemoryHistoricalIncidentAdapter implements HistoricalIncidentPort {

    private static final List<HistoricalIncident> DATA = List.of(
        new HistoricalIncident("INC-0412", ScenarioType.CHECKOUT_FAILURE,
            "Checkout com 5xx e timeouts de 30s após lentidão no PSP",
            "Retries sem backoff saturaram o pool Hikari durante a degradação do PSP",
            "Abertura do circuit breaker do PSP e backoff exponencial com jitter no cliente HTTP", RiskLevel.BAIXO),
        new HistoricalIncident("INC-0377", ScenarioType.CHECKOUT_FAILURE,
            "Queda de 40% na conversão com erros 504 no api-gateway",
            "Release com timeout do PSP configurado em 60s acima do timeout do gateway (30s)",
            "Rollback do release e alinhamento dos timeouts", RiskLevel.MEDIO),
        new HistoricalIncident("INC-0298", ScenarioType.FRAUDULENT_PRODUCT,
            "Descrições de produto com HTML ativo renderizadas no storefront",
            "Sanitização aplicada só no frontend do painel do seller; a API aceitava HTML cru",
            "Sanitização server-side (allowlist) e rescan do catálogo", RiskLevel.MEDIO),
        new HistoricalIncident("INC-0331", ScenarioType.FRAUDULENT_PRODUCT,
            "Seller publicou 12 anúncios com links de phishing no título",
            "Moderação assíncrona atrasada por fila acumulada",
            "Escalonamento do worker de moderação e suspensão do seller", RiskLevel.ALTO),
        new HistoricalIncident("INC-0355", ScenarioType.CREATOR_ATTRIBUTION,
            "Vendas por creator 15% abaixo do gateway após mudança no CDN",
            "Regra de cache do CDN ignorava a query string dos short links",
            "Ajuste da cache key para incluir utm_* e reconciliação em dry-run antes de gravar", RiskLevel.BAIXO),
        new HistoricalIncident("INC-0240", ScenarioType.CREATOR_ATTRIBUTION,
            "Cookie de atribuição não persistia em Safari/iOS",
            "ITP limitava a vida do cookie client-side para 7 dias",
            "Persistência server-side do click-id com janela de 30 dias", RiskLevel.MEDIO),
        new HistoricalIncident("INC-0389", ScenarioType.COMMISSION_PAYOUT,
            "Lote semanal de repasse expirou aos 60% com timeouts no banco",
            "Chunk único de 10k itens com lock longo no ledger",
            "Retomada a partir do checkpoint em chunks de 500, validada antes em dry-run", RiskLevel.MEDIO),
        new HistoricalIncident("INC-0301", ScenarioType.COMMISSION_PAYOUT,
            "Repasses duplicados para 38 creators após reexecução do lote",
            "Idempotency key gerada com timestamp, não com o ID do item",
            "Estorno dos duplicados e idempotency key determinística (lote+item)", RiskLevel.ALTO));

    @Override
    public List<HistoricalIncident> findByScenario(ScenarioType scenario, int limit) {
        return DATA.stream().filter(h -> h.scenario() == scenario).limit(limit).toList();
    }
}
