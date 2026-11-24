package com.youshop.copilot.domain.model;

public enum Rating {
    THUMBS_UP, THUMBS_DOWN;

    public static Rating parse(String raw) {
        if (raw == null) throw new IllegalArgumentException("rating é obrigatório");
        return switch (raw.strip().toUpperCase()) {
            case "👍", "UP", "THUMBS_UP", "+1" -> THUMBS_UP;
            case "👎", "DOWN", "THUMBS_DOWN", "-1" -> THUMBS_DOWN;
            default -> throw new IllegalArgumentException("rating inválido: use 👍 ou 👎 (aceita também UP/DOWN)");
        };
    }
}
