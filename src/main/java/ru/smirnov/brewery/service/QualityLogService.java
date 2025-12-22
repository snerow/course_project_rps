package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.QualityLog;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.BatchRepository;
import ru.smirnov.brewery.repository.QualityLogRepository;
import ru.smirnov.brewery.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class QualityLogService {
    
    private final QualityLogRepository logRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public QualityLogService(QualityLogRepository logRepository, BatchRepository batchRepository,
                             UserRepository userRepository) {
        this.logRepository = logRepository;
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }
    
    public List<QualityLog> getAllLogs() {
        return logRepository.findAll();
    }
    
    public List<QualityLog> getLogsByBatchId(Integer batchId) {
        return logRepository.findByBatch_BatchId(batchId);
    }
    
    public List<QualityLog> getLogsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByPeriod(startDate, endDate);
    }
    
    public QualityLog createLog(Integer batchId, String operationType, String message, Integer userId) {
        QualityLog log = new QualityLog();
        
        if (batchId != null) {
            Batch batch = batchRepository.findById(batchId).orElse(null);
            log.setBatch(batch);
        }
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }
        
        log.setOperationType(operationType);
        log.setMessage(message);
        log.setOperationDate(LocalDateTime.now());
        
        return logRepository.save(log);
    }
    
    public List<QualityLog> getLogsByBatch(Integer batchId) {
        return logRepository.findByBatch_BatchId(batchId);
    }
}

