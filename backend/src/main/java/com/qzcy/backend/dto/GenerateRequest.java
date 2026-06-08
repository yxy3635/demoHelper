package com.qzcy.backend.dto;

import jakarta.validation.constraints.Size;

/**
 * Matches the frontend GenerateRequest type.
 * At least one of intentText or imageBase64 must be present.
 * API config is optional — if omitted the server default is used.
 */
public record GenerateRequest(

        @Size(max = 5000, message = "描述不能超过 5000 个字符")
        String intentText,

        String imageBase64,

        // ── User-provided API configuration (optional) ──
        String apiKey,
        String baseUrl,
        String model,
        String provider    // "deepseek" | "openai" | "anthropic" | "custom"
) {
    public boolean hasText() {
        return intentText != null && !intentText.isBlank();
    }

    public boolean hasImage() {
        return imageBase64 != null && !imageBase64.isBlank();
    }

    public boolean isValid() {
        return hasText() || hasImage();
    }

    /** True when the user provided their own API key. */
    public boolean hasApiConfig() {
        return apiKey != null && !apiKey.isBlank();
    }
}
