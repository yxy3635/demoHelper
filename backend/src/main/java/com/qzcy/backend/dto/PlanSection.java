package com.qzcy.backend.dto;

/**
 * A single development-plan section with its AI-ready prompt.
 */
public record PlanSection(
        String id,
        int order,
        String title,
        String description,
        String prompt,
        int estimatedHours,
        Difficulty difficulty
) {}
