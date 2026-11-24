package com.youshop.copilot.domain.scenario;

import java.util.Objects;

/** Creator do Factory Method: subclasses decidem qual {@link IncidentScenario} instanciar. */
public abstract class ScenarioCreator {

    protected abstract IncidentScenario createScenario();

    public final IncidentScenario build() {
        return Objects.requireNonNull(createScenario(), "createScenario() não pode retornar null");
    }
}
