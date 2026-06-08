package com.qzcy.backend.service;

import com.qzcy.backend.dto.*;

import java.time.Instant;
import java.util.*;

/**
 * Shared mapping: raw AI output → {@link GenerateResponse}.
 * Also defines the structured-output records that the AI models
 * must populate via tool/function calling.
 */
final class PlanOutputHelper {

    private PlanOutputHelper() {}

    // ── ThreadLocal: capture tool-call output when AI uses function calling ──

    /**
     * When the AI responds via function calling (output_plan tool),
     * the structured JSON lands in the tool callback.  We capture it here
     * so the generator can retrieve it instead of parsing the final text
     * summary that the AI produces after seeing the tool result.
     */
    private static final ThreadLocal<PlanOutput> TOOL_OUTPUT_HOLDER = new ThreadLocal<>();

    static PlanOutput consumeToolOutput() {
        PlanOutput out = TOOL_OUTPUT_HOLDER.get();
        TOOL_OUTPUT_HOLDER.remove();
        return out;
    }

    // ── Structured-output types (used as tool input schemas) ──

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

    // ── Mapping ──

    static GenerateResponse enrich(PlanOutput raw) {
        String id = "gen-" + UUID.randomUUID().toString().substring(0, 8);

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

    // ── Lenient JSON parsing (handles LLM formatting quirks) ──

    static PlanOutput parseLenient(String raw) {
        String json = extractJson(raw);
        try {
            return LENIENT_MAPPER.readValue(json, PlanOutput.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response as PlanOutput: " + e.getMessage(), e);
        }
    }

    /**
     * Strip markdown code fences and any surrounding text, leaving only the JSON object.
     */
    private static String extractJson(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Empty AI response");
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

    // ── Tool callback (Spring AI 2.0 style) ──

    static class PlanOutputToolCallback implements org.springframework.ai.tool.ToolCallback {

        private static final org.springframework.ai.tool.definition.ToolDefinition DEFINITION =
                org.springframework.ai.tool.definition.DefaultToolDefinition.builder()
                        .name("output_plan")
                        .description("输出生成的开发计划，包含标题、描述和章节列表")
                        .inputSchema("""
                            {
                              "type": "object",
                              "properties": {
                                "title": {"type": "string", "description": "项目标题（简体中文）"},
                                "description": {"type": "string", "description": "项目一句话描述（简体中文）"},
                                "sections": {
                                  "type": "array",
                                  "description": "开发计划章节列表，4-8个",
                                  "items": {
                                    "type": "object",
                                    "properties": {
                                      "title": {"type": "string", "description": "章节标题（简体中文）"},
                                      "description": {"type": "string", "description": "章节简述（简体中文）"},
                                      "prompt": {"type": "string", "description": "完整的、可复制粘贴的AI编码提示词"},
                                      "estimatedHours": {"type": "integer", "description": "预估工时（小时）"},
                                      "difficulty": {"type": "string", "enum": ["beginner", "intermediate", "advanced"]}
                                    },
                                    "required": ["title", "description", "prompt", "estimatedHours", "difficulty"]
                                  }
                                }
                              },
                              "required": ["title", "description", "sections"]
                            }
                            """)
                        .build();

        @Override
        public org.springframework.ai.tool.definition.ToolDefinition getToolDefinition() {
            return DEFINITION;
        }

        @Override
        public String call(String toolInput) {
            // Capture the structured output so the generator can retrieve it.
            // The AI will also produce a text summary afterwards, but that
            // summary is not valid JSON — we must consume the tool arguments.
            try {
                PlanOutput parsed = parseLenient(toolInput);
                TOOL_OUTPUT_HOLDER.set(parsed);
            } catch (Exception e) {
                // If parsing fails, store null so the generator falls back
                // to content-based parsing.
                TOOL_OUTPUT_HOLDER.set(null);
            }
            return toolInput;
        }
    }
}
