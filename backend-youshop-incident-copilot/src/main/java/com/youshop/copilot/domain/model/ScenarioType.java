package com.youshop.copilot.domain.model;

import java.util.Arrays;

public enum ScenarioType {
    CHECKOUT_FAILURE(1),
    FRAUDULENT_PRODUCT(2),
    CREATOR_ATTRIBUTION(3),
    COMMISSION_PAYOUT(4);

    private final int id;

    ScenarioType(int id) { this.id = id; }

    public int id() { return id; }

    public static ScenarioType fromId(int id) {
        return Arrays.stream(values()).filter(t -> t.id == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cenário desconhecido: " + id));
    }
}
