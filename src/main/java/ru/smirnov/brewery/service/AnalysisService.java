package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Analysis;
import ru.smirnov.brewery.entity.DeviationAlert;
import ru.smirnov.brewery.entity.ProductionStage;
import ru.smirnov.brewery.entity.Sample;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.AnalysisRepository;
import ru.smirnov.brewery.repository.DeviationAlertRepository;
import ru.smirnov.brewery.repository.SampleRepository;
import ru.smirnov.brewery.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnalysisService {
    
    private final AnalysisRepository analysisRepository;
    private final SampleRepository sampleRepository;
    private final UserRepository userRepository;
    private final DeviationAlertRepository deviationAlertRepository;
    
    @Autowired
    public AnalysisService(AnalysisRepository analysisRepository, SampleRepository sampleRepository,
                          UserRepository userRepository, DeviationAlertRepository deviationAlertRepository) {
        this.analysisRepository = analysisRepository;
        this.sampleRepository = sampleRepository;
        this.userRepository = userRepository;
        this.deviationAlertRepository = deviationAlertRepository;
    }
    
    public List<Analysis> getAllAnalyses() {
        return analysisRepository.findAll();
    }
    
    public Optional<Analysis> getAnalysisById(Integer id) {
        return analysisRepository.findById(id);
    }
    
    public List<Analysis> getAnalysesBySampleId(Integer sampleId) {
        return analysisRepository.findBySample_SampleId(sampleId);
    }
    
    public List<Analysis> getAnalysesByBatchId(Integer batchId) {
        return analysisRepository.findByBatchId(batchId);
    }
    
    public Analysis createAnalysis(Integer sampleId, String analysisType, Double pH, 
                                  Double density, Double alcoholContent, Integer performedById) {
        Sample sample = sampleRepository.findById(sampleId)
            .orElseThrow(() -> new RuntimeException("Sample not found: " + sampleId));
        
        User performedBy = null;
        if (performedById != null) {
            performedBy = userRepository.findById(performedById)
                .orElse(null);
        }
        
        Analysis analysis = new Analysis();
        analysis.setSample(sample);
        analysis.setAnalysisType(analysisType);
        analysis.setPH(pH);
        analysis.setDensity(density);
        analysis.setAlcoholContent(alcoholContent);
        analysis.setPerformedBy(performedBy);
        
        // Проверка соответствия нормам
        analysis.checkStandards();
        
        // Если параметры вне норм, создаем предупреждение
        if (!analysis.getWithinNorms()) {
            createDeviationAlerts(analysis);
        }
        
        return analysisRepository.save(analysis);
    }
    
    private void createDeviationAlerts(Analysis analysis) {
        ProductionStage stage = analysis.getSample().getStage();
        
        if (analysis.getPH() != null && stage.getNormsPHMin() != null && stage.getNormsPHMax() != null) {
            if (analysis.getPH() < stage.getNormsPHMin() || analysis.getPH() > stage.getNormsPHMax()) {
                DeviationAlert alert = new DeviationAlert();
                alert.setSample(analysis.getSample());
                alert.setAnalysis(analysis);
                alert.setParameterName("pH");
                alert.setActualValue(analysis.getPH());
                alert.setNormMin(stage.getNormsPHMin());
                alert.setNormMax(stage.getNormsPHMax());
                alert.setMessage(String.format("Отклонение pH: %.2f (норма: %.2f - %.2f)", 
                    analysis.getPH(), stage.getNormsPHMin(), stage.getNormsPHMax()));
                alert.setNotified(false);
                deviationAlertRepository.save(alert);
            }
        }
        
        if (analysis.getDensity() != null && stage.getNormsDensityMin() != null && stage.getNormsDensityMax() != null) {
            if (analysis.getDensity() < stage.getNormsDensityMin() || analysis.getDensity() > stage.getNormsDensityMax()) {
                DeviationAlert alert = new DeviationAlert();
                alert.setSample(analysis.getSample());
                alert.setAnalysis(analysis);
                alert.setParameterName("density");
                alert.setActualValue(analysis.getDensity());
                alert.setNormMin(stage.getNormsDensityMin());
                alert.setNormMax(stage.getNormsDensityMax());
                alert.setMessage(String.format("Отклонение плотности: %.3f (норма: %.3f - %.3f)", 
                    analysis.getDensity(), stage.getNormsDensityMin(), stage.getNormsDensityMax()));
                alert.setNotified(false);
                deviationAlertRepository.save(alert);
            }
        }
        
        if (analysis.getAlcoholContent() != null && stage.getNormsAlcoholMin() != null && stage.getNormsAlcoholMax() != null) {
            if (analysis.getAlcoholContent() < stage.getNormsAlcoholMin() || analysis.getAlcoholContent() > stage.getNormsAlcoholMax()) {
                DeviationAlert alert = new DeviationAlert();
                alert.setSample(analysis.getSample());
                alert.setAnalysis(analysis);
                alert.setParameterName("alcohol");
                alert.setActualValue(analysis.getAlcoholContent());
                alert.setNormMin(stage.getNormsAlcoholMin());
                alert.setNormMax(stage.getNormsAlcoholMax());
                alert.setMessage(String.format("Отклонение алкоголя: %.2f%% (норма: %.2f%% - %.2f%%)", 
                    analysis.getAlcoholContent(), stage.getNormsAlcoholMin(), stage.getNormsAlcoholMax()));
                alert.setNotified(false);
                deviationAlertRepository.save(alert);
            }
        }
    }
    
    public void validateParameters(Analysis analysis) {
        analysis.validateParameters();
    }
    
    public void checkStandards(Analysis analysis) {
        analysis.checkStandards();
        analysisRepository.save(analysis);
    }
}

