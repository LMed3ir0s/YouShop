package com.youshop.copilot.adapter.in.web;

import com.youshop.copilot.adapter.in.web.ApiDtos.*;
import com.youshop.copilot.application.port.in.IncidentCopilotUseCase;
import com.youshop.copilot.domain.model.Rating;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentCopilotUseCase useCase;

    public IncidentController(IncidentCopilotUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{id}")
    public IncidentAnalysisDto get(@PathVariable String id) {
        return IncidentAnalysisDto.from(useCase.analyze(id));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<FeedbackResponse> feedback(@PathVariable String id, @Valid @RequestBody FeedbackRequest body) {
        var saved = useCase.registerFeedback(id, Rating.parse(body.rating()), body.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(FeedbackResponse.from(saved));
    }
}
