package com.youshop.copilot.domain.exception;

public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(String incidentId) {
        super("Incidente não encontrado: " + incidentId);
    }
}
