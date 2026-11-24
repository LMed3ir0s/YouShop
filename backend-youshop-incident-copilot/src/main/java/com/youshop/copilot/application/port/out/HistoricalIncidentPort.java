package com.youshop.copilot.application.port.out;

import com.youshop.copilot.domain.model.HistoricalIncident;
import com.youshop.copilot.domain.model.ScenarioType;

import java.util.List;

public interface HistoricalIncidentPort {
    List<HistoricalIncident> findByScenario(ScenarioType scenario, int limit);
}
