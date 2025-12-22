package ru.smirnov.brewery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.smirnov.brewery.entity.ProductionStage;

import java.util.Optional;

@Repository
public interface ProductionStageRepository extends JpaRepository<ProductionStage, Integer> {
    Optional<ProductionStage> findByStageName(String stageName);
}

