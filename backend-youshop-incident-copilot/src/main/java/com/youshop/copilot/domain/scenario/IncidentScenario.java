package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioInfo;
import com.youshop.copilot.domain.model.ScenarioType;

/** Produto do Factory Method: a definição de um cenário de incidente da YouShop. */
public interface IncidentScenario {
    ScenarioType type();
    ScenarioInfo info();
}
