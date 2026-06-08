package com.qzcy.backend.service;

import com.qzcy.backend.dto.*;

import java.time.Instant;
import java.util.*;

/**
 * Shared mapping: raw AI text output → {@link GenerateResponse}.
 * AI is instructed via system prompt to output JSON directly in the text
 * response (no function calling), which avoids thread-bound state issues.
 */
final class PlanOutputHelper {

    private PlanOutputHelper() {}

    // ── Structured-output types ──

    public record PlanOutput(
            String title,
            String description,
            List<SectionOutput> sections
    ) {}

    public record SectionOutput(
            String title,
            String description,
            String prompt,
            int estimatedHours,
            String difficulty
    ) {}

    // ── Progress callback (set by caller, called at key points) ──

    @FunctionalInterface
    interface ProgressCallback {
        void onProgress(int step);
    }

    private static ProgressCallback progressCallback;

    static void setProgressCallback(ProgressCallback cb) {
        progressCallback = cb;
    }

    static void clearProgressCallback() {
        progressCallback = null;
    }

    static void emitProgress(int step) {
        ProgressCallback cb = progressCallback;
        if (cb != null) {
            try {
                cb.onProgress(step);
            } catch (Exception ignored) { }
        }
    }

    // ── Mapping ──

    static GenerateResponse enrich(PlanOutput raw) {
        String id = "gen-" + UUID.randomUUID().toString();

        List<PlanSection> sections = new ArrayList<>();
        int totalHours = 0;
        Difficulty overall = Difficulty.beginner;

        for (int i = 0; i < raw.sections().size(); i++) {
            SectionOutput s = raw.sections().get(i);
            Difficulty diff = parseDifficulty(s.difficulty());
            int hours = s.estimatedHours();

            sections.add(new PlanSection(
                    "sec-" + (i + 1),
                    i + 1,
                    s.title(),
                    s.description(),
                    s.prompt(),
                    hours,
                    diff
            ));
            totalHours += hours;
            if (diff.ordinal() > overall.ordinal()) {
                overall = diff;
            }
        }

        return new GenerateResponse(
                id,
                raw.title(),
                raw.description(),
                sections,
                new ProjectMetadata(sections.size(), totalHours, overall, Instant.now())
        );
    }

    static String buildUserMessage(String intentText) {
        if (intentText == null || intentText.isBlank()) {
            return "请根据上传的图片（如设计稿、原型图）生成一个完整的开发计划。";
        }
        return "请为以下项目生成完整的开发计划：\n\n" + intentText;
    }

    private static Difficulty parseDifficulty(String s) {
        if (s == null) return Difficulty.beginner;
        return switch (s.toLowerCase()) {
            case "advanced" -> Difficulty.advanced;
            case "intermediate" -> Difficulty.intermediate;
            default -> Difficulty.beginner;
        };
    }

    // ── Lenient JSON parsing ──

    static PlanOutput parseLenient(String raw) {
        String json = extractJson(raw);
        try {
            return LENIENT_MAPPER.readValue(json, PlanOutput.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response as PlanOutput: " + e.getMessage(), e);
        }
    }

    private static String extractJson(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("AI 返回了空响应");
        }
        String s = raw.trim();
        // Remove ```json / ``` fences
        if (s.startsWith("```")) {
            int start = s.indexOf('\n');
            if (start > 0) {
                s = s.substring(start + 1);
            }
            int end = s.lastIndexOf("```");
            if (end > 0) {
                s = s.substring(0, end);
            }
        }
        // Narrow to outermost { ... }
        int braceStart = s.indexOf('{');
        int braceEnd = s.lastIndexOf('}');
        if (braceStart >= 0 && braceEnd > braceStart) {
            s = s.substring(braceStart, braceEnd + 1);
        } else {
            throw new RuntimeException(
                "AI 未返回有效的JSON计划。请重试或补充更详细的项目描述。AI 响应前100字: " +
                raw.substring(0, Math.min(100, raw.length()))
            );
        }
        return s.trim();
    }

    private static final tools.jackson.databind.ObjectMapper LENIENT_MAPPER =
            tools.jackson.databind.json.JsonMapper.builder()
                    .enable(tools.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                    .enable(tools.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                    .enable(tools.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA)
                    .enable(tools.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
                    .build();
}
