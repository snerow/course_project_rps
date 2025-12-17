package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.DeviationAlert;

import java.util.List;

@Repository
public interface DeviationAlertRepository extends JpaRepository<DeviationAlert, Integer> {
    List<DeviationAlert> findBySample_SampleId(Integer sampleId);
    
    List<DeviationAlert> findByNotified(Boolean notified);
}

