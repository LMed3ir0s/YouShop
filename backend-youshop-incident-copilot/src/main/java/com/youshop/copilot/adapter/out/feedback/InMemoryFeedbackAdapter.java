package com.youshop.copilot.adapter.out.feedback;

import com.youshop.copilot.application.port.out.FeedbackPort;
import com.youshop.copilot.domain.model.Feedback;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryFeedbackAdapter implements FeedbackPort {

    private final List<Feedback> store = new CopyOnWriteArrayList<>();

    @Override
    public Feedback save(Feedback feedback) {
        store.add(feedback);
        return feedback;
    }

    @Override
    public List<Feedback> findByIncidentId(String incidentId) {
        return store.stream().filter(f -> f.incidentId().equalsIgnoreCase(incidentId)).toList();
    }
}
