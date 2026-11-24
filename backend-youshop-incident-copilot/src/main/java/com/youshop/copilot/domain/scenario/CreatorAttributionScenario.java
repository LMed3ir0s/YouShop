package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioInfo;
import com.youshop.copilot.domain.model.ScenarioType;
import com.youshop.copilot.domain.model.Severity;

/** Cenário 3 — P2: Divergência de Atribuição de Vendas a Creators. */
public final class CreatorAttributionScenario implements IncidentScenario {

    public static final class Creator extends ScenarioCreator {
        @Override protected IncidentScenario createScenario() { return new CreatorAttributionScenario(); }
    }

    private static final ScenarioType TYPE = ScenarioType.CREATOR_ATTRIBUTION;
    private static final ScenarioInfo INFO = new ScenarioInfo(TYPE.id(), "Divergência de Atribuição de Vendas a Creators", Severity.P2,
            "Vendas atribuídas a creators estão ~23% abaixo do gateway por perda de parâmetros UTM.", "attribution-service");

    @Override public ScenarioType type() { return TYPE; }
    @Override public ScenarioInfo info() { return INFO; }
}
