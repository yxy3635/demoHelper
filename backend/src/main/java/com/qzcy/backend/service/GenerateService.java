package com.qzcy.backend.service;

import com.qzcy.backend.config.AiProperties;
import com.qzcy.backend.dto.GenerateRequest;
import com.qzcy.backend.dto.GenerateResponse;
import com.qzcy.backend.entity.GenerationHistory;
import com.qzcy.backend.repository.GenerationHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.qzcy.backend.service.PlanOutputHelper.PlanOutput;

@Service
public class GenerateService {

    private static final Logger log = LoggerFactory.getLogger(GenerateService.class);

    private final DirectAiClient aiClient;
    private final AiProperties props;
    private final GenerationHistoryRepository repository;
    private final ObjectMapper objectMapper;

    public GenerateService(DirectAiClient aiClient,
                           AiProperties props,
                           GenerationHistoryRepository repository,
                           ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.props = props;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    // ── Public API ──

    public GenerateResponse generate(GenerateRequest request) {
        String content = callAi(request);
        PlanOutput raw = PlanOutputHelper.parseLenient(content);
        GenerateResponse response = PlanOutputHelper.enrich(raw);
        persist(request, response);
        return response;
    }

    public SseEmitter generateStream(GenerateRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L);

        PlanOutputHelper.setProgressCallback(step -> {
            try {
                emitter.send(SseEmitter.event().name("progress").data(String.valueOf(step)));
            } catch (IOException e) {
                PlanOutputHelper.clearProgressCallback();
            }
        });

        try {
            PlanOutputHelper.emitProgress(0);

            String content = callAi(request);

            PlanOutputHelper.emitProgress(2);

            PlanOutput raw = PlanOutputHelper.parseLenient(content);
            GenerateResponse response = PlanOutputHelper.enrich(raw);

            emitter.send(SseEmitter.event().name("progress").data("3"));
            emitter.send(SseEmitter.event()
                    .name("result")
                    .data(objectMapper.writeValueAsString(response)));

            persist(request, response);
            emitter.complete();
        } catch (Exception e) {
            log.error("Streaming generation failed", e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"message\":\"" + escapeJson(e.getMessage()) + "\"}"));
                emitter.complete();
            } catch (IOException ignored) {
                emitter.completeWithError(e);
            }
        } finally {
            PlanOutputHelper.clearProgressCallback();
        }

        return emitter;
    }

    // ── Helpers ──

    private String callAi(GenerateRequest req) {
        String apiKey;
        String baseUrl;
        String model;

        if (req.hasApiConfig()) {
            apiKey = req.apiKey();
            baseUrl = req.baseUrl() != null && !req.baseUrl().isBlank()
                    ? req.baseUrl()
                    : defaultBaseUrl(req.provider());
            model = req.model() != null && !req.model().isBlank()
                    ? req.model()
                    : defaultModel(req.provider());
        } else {
            // Fallback: use environment-configured defaults
            apiKey = props.apiKey();
            baseUrl = props.baseUrl();
            model = props.model();
            if (apiKey == null || apiKey.isBlank()) {
                throw new RuntimeException("未配置 API Key。请在右上角设置中填写你的 API Key。");
            }
        }

        log.info("Calling AI: baseUrl={} model={}", baseUrl, model);
        return aiClient.chat(apiKey, baseUrl, model,
                props.defaultSystemPrompt(),
                PlanOutputHelper.buildUserMessage(req.intentText()));
    }

    private void persist(GenerateRequest request, GenerateResponse response) {
        try {
            GenerationHistory record = new GenerationHistory(
                    response.id(),
                    response.title(),
                    response.description(),
                    response.metadata().totalSections(),
                    response.metadata().estimatedTotalHours(),
                    response.metadata().difficulty(),
                    response.metadata().generatedAt(),
                    request.intentText(),
                    objectMapper.writeValueAsString(response)
            );
            repository.save(record);
            log.info("Saved generation id={}", response.id());
        } catch (Exception e) {
            log.error("Failed to persist generation record", e);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String defaultBaseUrl(String provider) {
        if (provider == null) return "https://api.deepseek.com";
        return switch (provider.toLowerCase()) {
            case "openai" -> "https://api.openai.com";
            default -> "https://api.deepseek.com";
        };
    }

    private static String defaultModel(String provider) {
        if (provider == null) return "deepseek-v4-flash";
        return switch (provider.toLowerCase()) {
            case "openai" -> "gpt-4o";
            default -> "deepseek-v4-flash";
        };
    }

    public List<GenerationHistory> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<GenerationHistory> getHistoryById(String id) {
        return repository.findById(id);
    }
}
