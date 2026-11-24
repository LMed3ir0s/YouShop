package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioInfo;
import com.youshop.copilot.domain.model.ScenarioType;
import com.youshop.copilot.domain.model.Severity;

/** Cenário 4 — P0: Falha no Repasse de Comissões. */
public final class CommissionPayoutScenario implements IncidentScenario {

    public static final class Creator extends ScenarioCreator {
        @Override protected IncidentScenario createScenario() { return new CommissionPayoutScenario(); }
    }

    private static final ScenarioType TYPE = ScenarioType.COMMISSION_PAYOUT;
    private static final ScenarioInfo INFO = new ScenarioInfo(TYPE.id(), "Falha no Repasse de Comissões", Severity.P0,
            "O lote semanal de repasse a creators está travado por timeout.", "payout-worker");

    @Override public ScenarioType type() { return TYPE; }
    @Override public ScenarioInfo info() { return INFO; }
}
