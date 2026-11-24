package com.youshop.copilot.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.youshop.copilot.domain.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class ApiDtos {
    private ApiDtos() {}

    public record ScenarioDto(int id, String name, Severity severity, String description, String affectedService) {
        public static ScenarioDto from(ScenarioInfo s) {
            return new ScenarioDto(s.id(), s.name(), s.severity(), s.description(), s.affectedService());
        }
    }

    public record LogDto(Instant timestamp, String level, String source, String message) {}

    public record MetricDto(String name, double value, String unit, String source) {}

    public record TelemetryDto(Instant collectedAt, Map<String, String> tags, List<LogDto> logs, List<MetricDto> metrics) {
        public static TelemetryDto from(TelemetrySnapshot t) {
            return new TelemetryDto(t.collectedAt(), t.tags(),
                    t.logs().stream().map(l -> new LogDto(l.timestamp(), l.level(), l.source(), l.message())).toList(),
                    t.metrics().stream().map(m -> new MetricDto(m.name(), m.value(), m.unit(), m.source())).toList());
        }
    }

    public record McpActionDto(String tool, Map<String, Object> arguments) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SolutionDto(int rank, String title, String description, RiskLevel risk,
                              boolean requiresHumanApproval, McpActionDto mcpAction) {
        static SolutionDto from(int rank, Solution s) {
            var action = s.mcpTool() == null ? null : new McpActionDto(s.mcpTool(), s.toolArguments());
            return new SolutionDto(rank, s.title(), s.description(), s.risk(), s.requiresHumanApproval(), action);
        }
    }

    public record AnalysisDto(String symptom, String rootCause, List<SolutionDto> solutions) {
        static AnalysisDto from(Diagnosis d) {
            return new AnalysisDto(d.symptom(), d.rootCause(), IntStream.range(0, d.solutions().size())
                    .mapToObj(i -> SolutionDto.from(i + 1, d.solutions().get(i))).toList());
        }
    }

    public record IncidentAnalysisDto(String incidentId, ScenarioDto scenario, TelemetryDto telemetry,
                                      AnalysisDto analysis, List<String> securityFlags, Instant analyzedAt) {
        public static IncidentAnalysisDto from(IncidentAnalysis a) {
            return new IncidentAnalysisDto(a.incidentId(), ScenarioDto.from(a.scenario()),
                    TelemetryDto.from(a.sanitizedTelemetry()), AnalysisDto.from(a.diagnosis()),
                    a.securityFlags(), a.analyzedAt());
        }
    }

    public record FeedbackRequest(@NotBlank(message = "rating é obrigatório (👍/👎)") String rating,
                                  @Size(max = 1000) String comment) {}

    public record FeedbackResponse(String incidentId, Rating rating, Instant recordedAt) {
        public static FeedbackResponse from(Feedback f) {
            return new FeedbackResponse(f.incidentId(), f.rating(), f.createdAt());
        }
    }
}
