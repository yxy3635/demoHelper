package com.qzcy.backend.service;

import com.qzcy.backend.dto.GenerateRequest;
import com.qzcy.backend.dto.GenerateResponse;
import com.qzcy.backend.entity.GenerationHistory;
import com.qzcy.backend.repository.GenerationHistoryRepository;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Orchestrates: validate → call AI provider → persist → return.
 */
@Service
public class GenerateService {

    private static final Logger log = LoggerFactory.getLogger(GenerateService.class);

    private final PlanGenerator planGenerator;
    private final GenerationHistoryRepository repository;
    private final ObjectMapper objectMapper;

    public GenerateService(PlanGenerator planGenerator,
                           GenerationHistoryRepository repository,
                           ObjectMapper objectMapper) {
        this.planGenerator = planGenerator;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public GenerateResponse generate(GenerateRequest request) {
        GenerateResponse response = planGenerator.generate(
                request.intentText(), request.imageBase64());

        // Persist asynchronously (best-effort)
        try {
            GenerationHistory record = new GenerationHistory(
                    response.id(),
                    response.title(),
                    response.description(),
                    response.metadata().totalSections(),
                    response.metadata().estimatedTotalHours(),
                    response.metadata().difficulty(),
                    response.metadata().generatedAt(),
                    request.intentText(),
                    objectMapper.writeValueAsString(response)
            );
            repository.save(record);
            log.info("Saved generation id={}", record.getId());
        } catch (Exception e) {
            log.error("Failed to persist generation record", e);
        }

        return response;
    }

    public List<GenerationHistory> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<GenerationHistory> getHistoryById(String id) {
        return repository.findById(id);
    }
}
