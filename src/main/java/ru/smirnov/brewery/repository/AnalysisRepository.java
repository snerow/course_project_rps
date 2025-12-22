package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.Analysis;

import java.util.List;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {
    List<Analysis> findBySample_SampleId(Integer sampleId);
    
    List<Analysis> findByWithinNorms(Boolean withinNorms);
    
    @Query("SELECT a FROM Analysis a WHERE a.sample.batch.batchId = :batchId")
    List<Analysis> findByBatchId(@Param("batchId") Integer batchId);
    
    @Query("SELECT a FROM Analysis a WHERE a.sample.sampleId IN :sampleIds")
    List<Analysis> findBySampleIds(@Param("sampleIds") List<Integer> sampleIds);
}

