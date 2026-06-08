package com.qzcy.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Server-side AI defaults.  When the user provides their own API key via the
 * frontend settings panel, those values take precedence.
 */
@ConfigurationProperties(prefix = "ai")
public record AiProperties(
        String provider,
        String defaultSystemPrompt,
        String apiKey,
        String baseUrl,
        String model
) {
    public AiProperties {
        if (provider == null || provider.isBlank()) {
            provider = "deepseek";
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

                Default to a Vue 3 + TypeScript stack unless the user specifies \
                otherwise. Prefer Vite. Prefer Tailwind CSS for styling. \
                Prefer Spring Boot for backends.

                MODIFICATION REQUESTS: If the user's message starts with **修改请求** \
                or contains modification instructions, you MUST still output a complete \
                plan. Do NOT ask clarifying questions or request more information — \
                the user has already provided their original project description and \
                previous plan in the message. Simply regenerate the entire plan with \
                the requested modifications applied.

                CRITICAL — OUTPUT FORMAT: You must respond with ONLY a valid JSON \
                object. No markdown code fences, no explanation text before or after. \
                The response must be EXACTLY:

                {"title":"...","description":"...","sections":[{"title":"...","description":"...","prompt":"...","estimatedHours":N,"difficulty":"beginner|intermediate|advanced"}]}

                Do NOT include any text before or after the JSON. The very first \
                character of your response must be { and the very last must be }.
                """;
        }
    }
}
