package com.youshop.copilot.application.port.out;

import com.youshop.copilot.domain.model.Feedback;

import java.util.List;

public interface FeedbackPort {
    Feedback save(Feedback feedback);
    List<Feedback> findByIncidentId(String incidentId);
}
