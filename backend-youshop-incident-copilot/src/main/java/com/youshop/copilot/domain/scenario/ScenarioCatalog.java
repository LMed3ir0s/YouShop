package com.youshop.copilot.domain.scenario;

import com.youshop.copilot.domain.model.ScenarioType;

import java.util.*;

public final class ScenarioCatalog {
    private final Map<ScenarioType, IncidentScenario> scenarios;

    public ScenarioCatalog(Collection<? extends ScenarioCreator> creators) {
        var map = new EnumMap<ScenarioType, IncidentScenario>(ScenarioType.class);
        creators.forEach(c -> {
            IncidentScenario s = c.build();
            map.put(s.type(), s);
        });
        this.scenarios = Collections.unmodifiableMap(map);
    }

    public IncidentScenario get(ScenarioType type) {
        return Optional.ofNullable(scenarios.get(type))
                .orElseThrow(() -> new IllegalStateException("Cenário não registrado: " + type));
    }

    public List<IncidentScenario> all() {
        return List.copyOf(scenarios.values());
    }
}
