package ru.smirnov.brewery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smirnov.brewery.entity.Analysis;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.Sample;
import ru.smirnov.brewery.repository.AnalysisRepository;
import ru.smirnov.brewery.repository.BatchRepository;
import ru.smirnov.brewery.repository.SampleRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {
    
    private final BatchRepository batchRepository;
    private final SampleRepository sampleRepository;
    private final AnalysisRepository analysisRepository;
    
    @Autowired
    public ReportService(BatchRepository batchRepository, SampleRepository sampleRepository,
                        AnalysisRepository analysisRepository) {
        this.batchRepository = batchRepository;
        this.sampleRepository = sampleRepository;
        this.analysisRepository = analysisRepository;
    }
    
    public Map<String, Object> generateQualityDynamicsReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Batch> batches = batchRepository.findByPeriod(startDate, endDate);
        
        if (batches.isEmpty()) {
            return Collections.singletonMap("error", "Нет данных за период");
        }
        
        List<Sample> samples = sampleRepository.findByPeriod(startDate, endDate);
        List<Integer> sampleIds = samples.stream()
            .map(Sample::getSampleId)
            .collect(Collectors.toList());
        
        List<Analysis> analyses = analysisRepository.findBySampleIds(sampleIds);
        
        Map<String, Object> report = new HashMap<>();
        report.put("period", Map.of("start", startDate, "end", endDate));
        report.put("totalBatches", batches.size());
        report.put("totalSamples", samples.size());
        report.put("totalAnalyses", analyses.size());
        
        // Расчет средних значений по дням
        Map<String, Map<String, Double>> dailyStats = calculateDailyStatistics(samples, analyses);
        report.put("dailyStatistics", dailyStats);
        
        // Расчет средних значений параметров
        Map<String, Double> averageParameters = calculateAverageParameters(analyses);
        report.put("averageParameters", averageParameters);
        
        // Группировка по этапам производства
        Map<String, List<Analysis>> analysesByStage = analyses.stream()
            .collect(Collectors.groupingBy(a -> a.getSample().getStage().getStageName()));
        report.put("analysesByStage", analysesByStage);
        
        return report;
    }
    
    private Map<String, Map<String, Double>> calculateDailyStatistics(List<Sample> samples, List<Analysis> analyses) {
        Map<String, Map<String, Double>> dailyStats = new LinkedHashMap<>();
        
        Map<String, List<Analysis>> analysesByDate = analyses.stream()
            .collect(Collectors.groupingBy(a -> 
                a.getSample().getSamplingDate().toLocalDate().toString()));
        
        for (Map.Entry<String, List<Analysis>> entry : analysesByDate.entrySet()) {
            List<Analysis> dayAnalyses = entry.getValue();
            Map<String, Double> dayStats = new HashMap<>();
            
            double avgPH = dayAnalyses.stream()
                .filter(a -> a.getPH() != null)
                .mapToDouble(Analysis::getPH)
                .average()
                .orElse(0.0);
            
            double avgDensity = dayAnalyses.stream()
                .filter(a -> a.getDensity() != null)
                .mapToDouble(Analysis::getDensity)
                .average()
                .orElse(0.0);
            
            double avgAlcohol = dayAnalyses.stream()
                .filter(a -> a.getAlcoholContent() != null)
                .mapToDouble(Analysis::getAlcoholContent)
                .average()
                .orElse(0.0);
            
            dayStats.put("avgPH", avgPH);
            dayStats.put("avgDensity", avgDensity);
            dayStats.put("avgAlcohol", avgAlcohol);
            dayStats.put("count", (double) dayAnalyses.size());
            
            dailyStats.put(entry.getKey(), dayStats);
        }
        
        return dailyStats;
    }
    
    private Map<String, Double> calculateAverageParameters(List<Analysis> analyses) {
        Map<String, Double> averages = new HashMap<>();
        
        double avgPH = analyses.stream()
            .filter(a -> a.getPH() != null)
            .mapToDouble(Analysis::getPH)
            .average()
            .orElse(0.0);
        
        double avgDensity = analyses.stream()
            .filter(a -> a.getDensity() != null)
            .mapToDouble(Analysis::getDensity)
            .average()
            .orElse(0.0);
        
        double avgAlcohol = analyses.stream()
            .filter(a -> a.getAlcoholContent() != null)
            .mapToDouble(Analysis::getAlcoholContent)
            .average()
            .orElse(0.0);
        
        long withinNormsCount = analyses.stream()
            .filter(a -> Boolean.TRUE.equals(a.getWithinNorms()))
            .count();
        
        averages.put("avgPH", avgPH);
        averages.put("avgDensity", avgDensity);
        averages.put("avgAlcohol", avgAlcohol);
        averages.put("withinNormsPercentage", analyses.isEmpty() ? 0.0 : 
            (double) withinNormsCount / analyses.size() * 100);
        
        return averages;
    }
    
    public Map<String, Object> generateDeviationsReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sample> samples = sampleRepository.findByPeriod(startDate, endDate);
        List<Integer> sampleIds = samples.stream()
            .map(Sample::getSampleId)
            .collect(Collectors.toList());
        
        List<Analysis> analyses = analysisRepository.findBySampleIds(sampleIds);
        List<Analysis> deviations = analyses.stream()
            .filter(a -> Boolean.FALSE.equals(a.getWithinNorms()))
            .collect(Collectors.toList());
        
        Map<String, Object> report = new HashMap<>();
        report.put("period", Map.of("start", startDate, "end", endDate));
        report.put("totalDeviations", deviations.size());
        report.put("deviations", deviations);
        
        return report;
    }
}

