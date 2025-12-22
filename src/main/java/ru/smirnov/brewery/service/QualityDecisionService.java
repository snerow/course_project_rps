package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.QualityDecision;
import ru.smirnov.brewery.entity.QualityLog;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.BatchRepository;
import ru.smirnov.brewery.repository.QualityDecisionRepository;
import ru.smirnov.brewery.repository.QualityLogRepository;
import ru.smirnov.brewery.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QualityDecisionService {
    
    private final QualityDecisionRepository decisionRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    private final QualityLogRepository logRepository;
    
    @Autowired
    public QualityDecisionService(QualityDecisionRepository decisionRepository, 
                                 BatchRepository batchRepository,
                                 UserRepository userRepository,
                                 QualityLogRepository logRepository) {
        this.decisionRepository = decisionRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
        this.logRepository = logRepository;
    }
    
    public List<QualityDecision> getAllDecisions() {
        return decisionRepository.findAll();
    }
    
    public Optional<QualityDecision> getDecisionById(Integer id) {
        return decisionRepository.findById(id);
    }
    
    public List<QualityDecision> getDecisionsByBatchId(Integer batchId) {
        return decisionRepository.findByBatch_BatchId(batchId);
    }
    
    public QualityDecision approveBatch(Integer batchId, String comment, Integer decidedById) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        
        User decidedBy = null;
        if (decidedById != null) {
            decidedBy = userRepository.findById(decidedById)
                .orElse(null);
        }
        
        QualityDecision decision = new QualityDecision();
        decision.setBatch(batch);
        decision.setDecision("APPROVED");
        decision.setComment(comment);
        decision.setApproved(true);
        decision.setDecidedBy(decidedBy);
        
        batch.setStatus("APPROVED");
        batchRepository.save(batch);
        
        // Логирование
        QualityLog log = new QualityLog();
        log.setBatch(batch);
        log.setOperationType("BATCH_APPROVED");
        log.setMessage("Партия одобрена: " + batch.getBatchNumber());
        log.setUser(decidedBy);
        logRepository.save(log);
        
        return decisionRepository.save(decision);
    }
    
    public QualityDecision rejectBatch(Integer batchId, String comment, Integer decidedById) {
        Batch batch = batchRepository.findById(batchId)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));
        
        User decidedBy = null;
        if (decidedById != null) {
            decidedBy = userRepository.findById(decidedById)
                .orElse(null);
        }
        
        QualityDecision decision = new QualityDecision();
        decision.setBatch(batch);
        decision.setDecision("REJECTED");
        decision.setComment(comment);
        decision.setApproved(false);
        decision.setDecidedBy(decidedBy);
        
        batch.setStatus("REJECTED");
        batchRepository.save(batch);
        
        // Логирование
        QualityLog log = new QualityLog();
        log.setBatch(batch);
        log.setOperationType("BATCH_REJECTED");
        log.setMessage("Партия отклонена: " + batch.getBatchNumber() + ". Причина: " + comment);
        log.setUser(decidedBy);
        logRepository.save(log);
        
        return decisionRepository.save(decision);
    }
    
    public void approvePartial(Integer decisionId) {
        QualityDecision decision = decisionRepository.findById(decisionId)
            .orElseThrow(() -> new RuntimeException("Decision not found: " + decisionId));
        decision.approvePartial();
        decisionRepository.save(decision);
    }
    
    public void rejectPartial(Integer decisionId) {
        QualityDecision decision = decisionRepository.findById(decisionId)
            .orElseThrow(() -> new RuntimeException("Decision not found: " + decisionId));
        decision.rejectPartial();
        decisionRepository.save(decision);
    }
}

