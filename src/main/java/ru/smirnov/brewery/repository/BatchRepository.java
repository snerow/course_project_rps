package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.Batch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {
    Optional<Batch> findByBatchNumber(String batchNumber);
    
    List<Batch> findByStatus(String status);
    
    @Query("SELECT b FROM Batch b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Batch> findByPeriod(@Param("startDate") LocalDateTime startDate, 
                            @Param("endDate") LocalDateTime endDate);
}

