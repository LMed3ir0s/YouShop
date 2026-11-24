package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioInfo;
import com.youshop.copilot.domain.model.ScenarioType;
import com.youshop.copilot.domain.model.Severity;

/** Cenário 2 — P1: Fraude em Cadastro de Produto. */
public final class FraudulentProductScenario implements IncidentScenario {

    public static final class Creator extends ScenarioCreator {
        @Override protected IncidentScenario createScenario() { return new FraudulentProductScenario(); }
    }

    private static final ScenarioType TYPE = ScenarioType.FRAUDULENT_PRODUCT;
    private static final ScenarioInfo INFO = new ScenarioInfo(TYPE.id(), "Fraude em Cadastro de Produto", Severity.P1,
            "Produtos com script injetado em descrições estão publicados no catálogo (stored XSS).", "catalog-service");

    @Override public ScenarioType type() { return TYPE; }
    @Override public ScenarioInfo info() { return INFO; }
}
