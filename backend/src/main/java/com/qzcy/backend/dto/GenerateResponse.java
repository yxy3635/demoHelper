package com.qzcy.backend.dto;

import java.util.List;

/**
 * Matches the frontend GenerateResponse type exactly.
 */
public record GenerateResponse(
        String id,
        String title,
        String description,
        List<PlanSection> sections,
        ProjectMetadata metadata
) {}
