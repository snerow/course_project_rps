package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.Sample;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Integer> {
    List<Sample> findByBatch_BatchId(Integer batchId);
    
    List<Sample> findByStatus(String status);
    
    @Query("SELECT s FROM Sample s WHERE s.batch.batchId = :batchId AND s.stage.stageId = :stageId")
    List<Sample> findByBatchAndStage(@Param("batchId") Integer batchId, 
                                     @Param("stageId") Integer stageId);
    
    @Query("SELECT s FROM Sample s WHERE s.samplingDate BETWEEN :startDate AND :endDate")
    List<Sample> findByPeriod(@Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);
}

