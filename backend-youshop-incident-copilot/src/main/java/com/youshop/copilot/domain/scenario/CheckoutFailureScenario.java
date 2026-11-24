package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioInfo;
import com.youshop.copilot.domain.model.ScenarioType;
import com.youshop.copilot.domain.model.Severity;

/** Cenário 1 — P0: Falha no Checkout. */
public final class CheckoutFailureScenario implements IncidentScenario {

    public static final class Creator extends ScenarioCreator {
        @Override protected IncidentScenario createScenario() { return new CheckoutFailureScenario(); }
    }

    private static final ScenarioType TYPE = ScenarioType.CHECKOUT_FAILURE;
    private static final ScenarioInfo INFO = new ScenarioInfo(TYPE.id(), "Falha no Checkout", Severity.P0,
            "Gateway Timeout no checkout: a maioria dos compradores não consegue concluir o pagamento.", "checkout-service");

    @Override public ScenarioType type() { return TYPE; }
    @Override public ScenarioInfo info() { return INFO; }
}
