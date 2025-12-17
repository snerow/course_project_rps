package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.QualityDecision;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualityDecisionRepository extends JpaRepository<QualityDecision, Integer> {
    List<QualityDecision> findByBatch_BatchId(Integer batchId);
    
    Optional<QualityDecision> findByBatch_BatchIdAndApproved(Integer batchId, Boolean approved);
}

