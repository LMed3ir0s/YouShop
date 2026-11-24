package com.youshop.copilot.application.port.in;

import com.youshop.copilot.domain.model.*;

import java.util.List;

public interface IncidentCopilotUseCase {
    List<ScenarioInfo> listScenarios();
    IncidentAnalysis analyze(String incidentId);
    Feedback registerFeedback(String incidentId, Rating rating, String comment);
}
