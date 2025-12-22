package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.QualityLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QualityLogRepository extends JpaRepository<QualityLog, Integer> {
    List<QualityLog> findByBatch_BatchId(Integer batchId);
    
    @Query("SELECT ql FROM QualityLog ql WHERE ql.operationDate BETWEEN :startDate AND :endDate ORDER BY ql.operationDate DESC")
    List<QualityLog> findByPeriod(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
}

