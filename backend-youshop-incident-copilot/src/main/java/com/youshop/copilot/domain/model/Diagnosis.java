package com.youshop.copilot.domain.model;

import java.util.List;

public record Diagnosis(String symptom, String rootCause, List<Solution> solutions) {
    public Diagnosis {
        solutions = solutions == null ? List.of() : List.copyOf(solutions);
    }
}
