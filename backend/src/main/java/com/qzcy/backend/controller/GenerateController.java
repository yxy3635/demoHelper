package com.qzcy.backend.controller;

import com.qzcy.backend.dto.GenerateRequest;
import com.qzcy.backend.dto.GenerateResponse;
import com.qzcy.backend.entity.GenerationHistory;
import com.qzcy.backend.service.GenerateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GenerateController {

    private final GenerateService generateService;

    public GenerateController(GenerateService generateService) {
        this.generateService = generateService;
    }

    /**
     * Generate a development plan from user intent.
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(@Valid @RequestBody GenerateRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("请至少输入文字描述或上传一张图片");
        }
        GenerateResponse response = generateService.generate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * List all past generations (summary only, no full JSON).
     */
    @GetMapping("/history")
    public ResponseEntity<List<GenerationHistory>> getHistory() {
        List<GenerationHistory> history = generateService.getHistory();
        // Strip heavy response_json payload from list view
        history.forEach(h -> h.setResponseJson(null));
        return ResponseEntity.ok(history);
    }

    /**
     * Retrieve a specific generation with full response JSON.
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<GenerationHistory> getHistoryById(@PathVariable String id) {
        return generateService.getHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Health check.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
