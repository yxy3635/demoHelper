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
 * OpenAI-backed plan generator.
 * <p>
 * Uses Spring AI {@link ChatClient} with function calling to force
 * structured JSON output from the model.
 */
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
public class OpenAiPlanGenerator implements PlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(OpenAiPlanGenerator.class);

    private final ChatClient chatClient;
    private final AiProperties props;

    public OpenAiPlanGenerator(ChatModel chatModel, AiProperties props) {
        this.props = props;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(new PlanOutputHelper.PlanOutputToolCallback())
                .build();
    }

    @Override
    public GenerateResponse generate(String intentText, String imageBase64) {
        log.info("OpenAI via Spring AI — generating plan with function calling");

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
