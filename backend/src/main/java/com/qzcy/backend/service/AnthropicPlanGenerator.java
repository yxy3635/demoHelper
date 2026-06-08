package com.qzcy.backend.service;

import com.qzcy.backend.config.AiProperties;
import com.qzcy.backend.dto.GenerateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.qzcy.backend.service.PlanOutputHelper.PlanOutput;

/**
 * Anthropic-backed plan generator.
 * <p>
 * Uses Spring AI {@link ChatClient} with a tool definition that forces
 * Claude to call {@code tool_use} — giving us guaranteed structured output.
 */
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "anthropic", matchIfMissing = true)
@ConditionalOnProperty(name = "spring.ai.anthropic.api-key")
public class AnthropicPlanGenerator implements PlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(AnthropicPlanGenerator.class);

    private final ChatClient chatClient;
    private final AiProperties props;

    public AnthropicPlanGenerator(ChatModel chatModel, AiProperties props) {
        this.props = props;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(new PlanOutputHelper.PlanOutputToolCallback())
                .build();
    }

    @Override
    public GenerateResponse generate(String intentText, String imageBase64) {
        log.info("Anthropic via Spring AI — generating plan with tool_use");

        String content = chatClient.prompt()
                .system(props.defaultSystemPrompt())
                .user(PlanOutputHelper.buildUserMessage(intentText))
                .call()
                .content();

        log.debug("{} raw response: {}", props.provider(), content);

        // Try tool-call output first (function calling path), fall back to
        // content-based parsing (direct JSON path).
        PlanOutput raw = PlanOutputHelper.consumeToolOutput();
        if (raw != null) {
            log.info("Using output from tool callback (function calling path)");
            return PlanOutputHelper.enrich(raw);
        }

        log.info("Falling back to content-based JSON parsing");
        raw = PlanOutputHelper.parseLenient(content);
        return PlanOutputHelper.enrich(raw);
    }
}
