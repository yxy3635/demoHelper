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
 * DeepSeek-backed plan generator (OpenAI-compatible API).
 * <p>
 * Uses Spring AI's DeepSeek starter which auto-configures a
 * {@link ChatModel} bean via {@code spring.ai.deepseek.*} properties.
 */
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "deepseek")
@ConditionalOnProperty(name = "spring.ai.deepseek.api-key")
public class DeepSeekPlanGenerator implements PlanGenerator {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekPlanGenerator.class);

    private final ChatClient chatClient;
    private final AiProperties props;

    public DeepSeekPlanGenerator(ChatModel chatModel, AiProperties props) {
        this.props = props;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultToolCallbacks(new PlanOutputHelper.PlanOutputToolCallback())
                .build();
    }

    @Override
    public GenerateResponse generate(String intentText, String imageBase64) {
        log.info("DeepSeek via Spring AI — generating plan with function calling");

        String content = chatClient.prompt()
                .system(props.defaultSystemPrompt())
                .user(PlanOutputHelper.buildUserMessage(intentText))
                .call()
                .content();

        log.debug("DeepSeek raw response: {}", content);

        // When the AI uses function calling (output_plan tool), the structured
        // JSON is captured by the tool callback and stored in a ThreadLocal.
        // .content() only returns the final text summary, which is NOT JSON.
        // We must try the captured tool output first.
        PlanOutput raw = PlanOutputHelper.consumeToolOutput();
        if (raw != null) {
            log.info("Using output from tool callback (function calling path)");
            return PlanOutputHelper.enrich(raw);
        }

        // Fallback: AI returned JSON directly in the text content
        // (non-function-calling path).
        log.info("Falling back to content-based JSON parsing");
        raw = PlanOutputHelper.parseLenient(content);
        return PlanOutputHelper.enrich(raw);
    }
}
