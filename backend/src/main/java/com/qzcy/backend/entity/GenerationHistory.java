package com.qzcy.backend.entity;

import com.qzcy.backend.dto.Difficulty;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import java.time.Instant;

/**
 * Persisted generation record so users can revisit past plans.
 * Implements {@link Persistable} with {@code isNew() == true} so Spring Data
 * JPA always calls {@code entityManager.persist()} rather than {@code merge()}.
 * This avoids optimistic-locking / stale-state errors when the primary key is
 * assigned manually (e.g. {@code gen-<uuid>}) while {@code @GeneratedValue}
 * is also present.
 */
@Entity
@Table(name = "generation_history")
public class GenerationHistory implements Persistable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "total_sections", nullable = false)
    private int totalSections;

    @Column(name = "estimated_total_hours", nullable = false)
    private int estimatedTotalHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "intent_text", columnDefinition = "TEXT")
    private String intentText;

    @Column(name = "response_json", columnDefinition = "MEDIUMTEXT")
    private String responseJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // ── Constructors ──

    public GenerationHistory() {}

    public GenerationHistory(String id, String title, String description,
                             int totalSections, int estimatedTotalHours,
                             Difficulty difficulty, Instant generatedAt,
                             String intentText, String responseJson) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalSections = totalSections;
        this.estimatedTotalHours = estimatedTotalHours;
        this.difficulty = difficulty;
        this.generatedAt = generatedAt;
        this.intentText = intentText;
        this.responseJson = responseJson;
    }

    // ── Persistable ──

    /**
     * Always returns {@code true} because the ID is assigned manually from
     * {@code GenerateResponse.id()} (e.g. {@code gen-<uuid>}) and every
     * record is new at persist time.  This forces Spring Data JPA to call
     * {@code entityManager.persist()} instead of {@code merge()}, avoiding
     * stale-state / optimistic-locking errors caused by the mix of a manual
     * ID and the {@code @GeneratedValue} annotation.
     */
    @Override
    public boolean isNew() {
        return true;
    }

    // ── Getters / Setters ──

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalSections() { return totalSections; }
    public void setTotalSections(int totalSections) { this.totalSections = totalSections; }

    public int getEstimatedTotalHours() { return estimatedTotalHours; }
    public void setEstimatedTotalHours(int estimatedTotalHours) { this.estimatedTotalHours = estimatedTotalHours; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public String getIntentText() { return intentText; }
    public void setIntentText(String intentText) { this.intentText = intentText; }

    public String getResponseJson() { return responseJson; }
    public void setResponseJson(String responseJson) { this.responseJson = responseJson; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
