package com.youshop.copilot.config;

import com.youshop.copilot.application.mcp.McpToolRegistry;
import com.youshop.copilot.application.pipeline.AiPipeline;
import com.youshop.copilot.application.pipeline.middleware.*;
import com.youshop.copilot.application.port.out.HistoricalIncidentPort;
import com.youshop.copilot.application.port.out.LlmPort;
import com.youshop.copilot.domain.scenario.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.youshop.copilot.domain.scenario.fixture.FixtureCatalog;

import java.util.List;

@Configuration
public class CopilotConfig {

    @Bean
    ScenarioCatalog scenarioCatalog() {
        return new ScenarioCatalog(List.of(
                new CheckoutFailureScenario.Creator(),
                new FraudulentProductScenario.Creator(),
                new CreatorAttributionScenario.Creator(),
                new CommissionPayoutScenario.Creator()));
    }

    @Bean
    McpToolRegistry mcpToolRegistry() {
        return new McpToolRegistry();
    }

    /** A ordem dos elos importa: mascarar -> neutralizar -> few-shot -> política (que também valida a saída). */
    @Bean
    AiPipeline aiPipeline(LlmPort llm, HistoricalIncidentPort history, McpToolRegistry tools) {
        return new AiPipeline(List.of(
                new DataMaskingMiddleware(),
                new PromptInjectionGuardMiddleware(),
                new FewShotContextMiddleware(history),
                new TroubleshootingPolicyMiddleware(tools)), llm);
    }

    @Bean
    FixtureCatalog fixtureCatalog() {
        return new FixtureCatalog();
    }
}
