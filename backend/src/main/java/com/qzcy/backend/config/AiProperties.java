package com.qzcy.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provider selection.  Actual API keys / model settings are managed by
 * Spring AI auto-configuration ({@code spring.ai.anthropic.*} /
 * {@code spring.ai.openai.*}).
 */
@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        Provider provider,
        String defaultSystemPrompt
) {
    public enum Provider {
        anthropic,
        openai,
        deepseek
    }

    public AiProperties {
        if (provider == null) {
            provider = Provider.deepseek;
        }
        if (defaultSystemPrompt == null || defaultSystemPrompt.isBlank()) {
            defaultSystemPrompt = """
                You are a senior software architect and development planner. \
                Your job is to take a user's project idea and produce a detailed, \
                actionable development plan.

                For each plan you create:
                - Break the project into 4-8 logical, sequential sections.
                - Each section should represent a meaningful chunk of work.
                - For each section, write a COMPLETE, copy-paste-ready AI coding \
                  prompt that the user can send to an AI coding assistant. The prompt \
                  should be very detailed — specify file names, folder structures, \
                  libraries, TypeScript types, error handling, edge cases, and \
                  everything the AI would need to implement that section in one shot.
                - Estimate realistic hours for each section.
                - Assign a difficulty: beginner / intermediate / advanced.
                - Provide a compelling title and a one-paragraph description of the \
                  whole project.
                - The title and all content MUST be in Chinese (Simplified / 简体中文).

                Default to a React + TypeScript stack unless the user specifies \
                otherwise. Prefer Vite over CRA. Prefer Tailwind CSS for styling. \
                Prefer Firebase/Supabase for quick backends.

                IMPORTANT: You must output ONLY valid JSON without any markdown \
                code fences (no ```json blocks). The response must be a single \
                JSON object with title, description, and sections fields. \
                All Chinese characters inside JSON string values must be properly \
                escaped according to the JSON standard.
                """;
        }
    }
}
