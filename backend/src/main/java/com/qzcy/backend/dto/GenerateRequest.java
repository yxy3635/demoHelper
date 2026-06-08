package com.qzcy.backend.dto;

import jakarta.validation.constraints.Size;

/**
 * Matches the frontend GenerateRequest type.
 * At least one of intentText or imageBase64 must be present.
 */
public record GenerateRequest(

        @Size(max = 5000, message = "描述不能超过 5000 个字符")
        String intentText,

        String imageBase64
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
}
