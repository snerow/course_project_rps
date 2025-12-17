package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.ProductionStage;
import ru.smirnov.brewery.entity.Sample;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.BatchRepository;
import ru.smirnov.brewery.repository.ProductionStageRepository;
import ru.smirnov.brewery.repository.SampleRepository;
import ru.smirnov.brewery.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SampleService {
    
    private final SampleRepository sampleRepository;
    private final BatchRepository batchRepository;
    private final ProductionStageRepository stageRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public SampleService(SampleRepository sampleRepository, BatchRepository batchRepository,
                        ProductionStageRepository stageRepository, UserRepository userRepository) {
        this.sampleRepository = sampleRepository;
        this.batchRepository = batchRepository;
        this.stageRepository = stageRepository;
        this.userRepository = userRepository;
    }
    
    public List<Sample> getAllSamples() {
        return sampleRepository.findAll();
    }
    
    public Optional<Sample> getSampleById(Integer id) {
        return sampleRepository.findById(id);
    }
    
    public List<Sample> getSamplesByBatchId(Integer batchId) {
        return sampleRepository.findByBatch_BatchId(batchId);
    }
    
    public List<Sample> getSamplesByStatus(String status) {
        return sampleRepository.findByStatus(status);
    }
    
    public List<Sample> getSamplesByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return sampleRepository.findByPeriod(startDate, endDate);
    }
    
    public Sample createSample(Integer batchId, Integer stageId, Integer collectedById) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        
        ProductionStage stage = stageRepository.findById(stageId)
            .orElseThrow(() -> new RuntimeException("Stage not found: " + stageId));
        
        User collectedBy = null;
        if (collectedById != null) {
            collectedBy = userRepository.findById(collectedById)
                .orElse(null);
        }
        
        Sample sample = new Sample();
        sample.setBatch(batch);
        sample.setStage(stage);
        sample.setSamplingDate(LocalDateTime.now());
        sample.setStatus("REGISTERED");
        sample.setCollectedBy(collectedBy);
        
        return sampleRepository.save(sample);
    }
    
    public Sample updateSampleStatus(Integer sampleId, String status) {
        Sample sample = sampleRepository.findById(sampleId)
            .orElseThrow(() -> new RuntimeException("Sample not found: " + sampleId));
        sample.setStatus(status);
        return sampleRepository.save(sample);
    }
    
    public void collectSample(Integer sampleId) {
        Sample sample = sampleRepository.findById(sampleId)
            .orElseThrow(() -> new RuntimeException("Sample not found: " + sampleId));
        sample.collectSample();
    }
}

