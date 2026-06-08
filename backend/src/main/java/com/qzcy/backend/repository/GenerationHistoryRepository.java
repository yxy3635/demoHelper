package com.qzcy.backend.repository;

import com.qzcy.backend.entity.GenerationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenerationHistoryRepository extends JpaRepository<GenerationHistory, String> {

    List<GenerationHistory> findAllByOrderByCreatedAtDesc();
}
