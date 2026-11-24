package com.youshop.copilot.adapter.in.web;

import com.youshop.copilot.application.port.in.IncidentCopilotUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scenarios")
public class ScenarioController {

    private final IncidentCopilotUseCase useCase;

    public ScenarioController(IncidentCopilotUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public List<ApiDtos.ScenarioDto> list() {
        return useCase.listScenarios().stream().map(ApiDtos.ScenarioDto::from).toList();
    }
}
