package com.qzcy.backend.service;

import com.qzcy.backend.dto.GenerateResponse;

/**
 * Abstraction over different AI providers.
 * Implementations use Spring AI's ChatClient with tool-calling
 * to force structured output.
 */
@FunctionalInterface
public interface PlanGenerator {

    /**
     * Generate a structured development plan from user intent.
     *
     * @param intentText  the user's natural-language project description
     * @param imageBase64 optional base64-encoded image (e.g. design mockup)
     * @return a fully-populated {@link GenerateResponse}
     */
    GenerateResponse generate(String intentText, String imageBase64);
}
