package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.BatchRepository;
import ru.smirnov.brewery.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BatchService {
    
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public BatchService(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }
    
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }
    
    public Optional<Batch> getBatchById(Integer id) {
        return batchRepository.findById(id);
    }
    
    public Optional<Batch> getBatchByNumber(String batchNumber) {
        return batchRepository.findByBatchNumber(batchNumber);
    }
    
    public List<Batch> getBatchesByStatus(String status) {
        return batchRepository.findByStatus(status);
    }
    
    public List<Batch> getBatchesByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return batchRepository.findByPeriod(startDate, endDate);
    }
    
    public Batch createBatch(String batchNumber, String productType, Integer createdById) {
        User createdBy = null;
        if (createdById != null) {
            createdBy = userRepository.findById(createdById)
                .orElse(null);
        }
        
        Batch batch = new Batch();
        batch.setBatchNumber(batchNumber);
        batch.setProductType(productType);
        batch.setStatus("CREATED");
        batch.setCreatedBy(createdBy);
        
        return batchRepository.save(batch);
    }
    
    public Batch updateBatchStatus(Integer batchId, String status) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        batch.setStatus(status);
        return batchRepository.save(batch);
    }
    
    public void registerSample(Integer batchId) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        batch.registerSample();
    }
}

