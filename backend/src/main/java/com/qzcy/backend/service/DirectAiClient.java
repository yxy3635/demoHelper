package com.qzcy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Calls OpenAI-compatible chat-completion APIs directly via HTTP.
 * Works with DeepSeek, OpenAI, and any OpenAI-compatible provider.
 * <p>
 * Avoids Spring AI's internal ChatModel/OpenAIClient APIs so we can use
 * user-provided API keys dynamically.
 */
@Component
public class DirectAiClient {

    private static final Logger log = LoggerFactory.getLogger(DirectAiClient.class);

    private final RestClient.Builder restClientBuilder;

    public DirectAiClient(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    /**
     * Call the chat-completions API and return the assistant's text content.
     */
    public String chat(String apiKey, String baseUrl, String model,
                       String systemPrompt, String userMessage) {
        String url = baseUrl.replaceAll("/+$", "") + "/v1/chat/completions";

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "temperature", 0.7,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        log.debug("Calling AI API: url={} model={}", url, model);

        RestClient client = restClientBuilder
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        @SuppressWarnings("unchecked")
        Map<String, Object> response = client.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException("AI API 返回空响应");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("AI API 返回的 choices 为空");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null) {
            throw new RuntimeException("AI API 返回的 message 为空");
        }

        String content = (String) message.get("content");
        if (content == null) {
            throw new RuntimeException("AI API 返回的 content 为空");
        }

        return content;
    }
}
