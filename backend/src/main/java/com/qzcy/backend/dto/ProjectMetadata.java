package com.qzcy.backend.dto;

import java.time.Instant;

public record ProjectMetadata(
        int totalSections,
        int estimatedTotalHours,
        Difficulty difficulty,
        Instant generatedAt
) {}
