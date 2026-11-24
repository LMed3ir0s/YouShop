package com.youshop.copilot.domain.scenario.fixture;

import com.youshop.copilot.domain.model.ScenarioType;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Reúne as fixtures por tipo de cenário. O construtor sem argumentos registra as 4 fixtures do MVP. */
public final class FixtureCatalog {
    private final Map<ScenarioType, ScenarioFixture> fixtures;

    public FixtureCatalog() {
        this(List.of(new CheckoutFailureFixture(), new FraudulentProductFixture(),
                new CreatorAttributionFixture(), new CommissionPayoutFixture()));
    }

    public FixtureCatalog(Collection<? extends ScenarioFixture> fixtures) {
        var map = new EnumMap<ScenarioType, ScenarioFixture>(ScenarioType.class);
        fixtures.forEach(f -> map.put(f.type(), f));
        this.fixtures = Collections.unmodifiableMap(map);
    }

    public ScenarioFixture get(ScenarioType type) {
        return Optional.ofNullable(fixtures.get(type))
                .orElseThrow(() -> new IllegalStateException("Fixture não registrada: " + type));
    }
}
