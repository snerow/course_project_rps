package ru.smirnov.brewery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.QualityDecision;
import ru.smirnov.brewery.entity.QualityLog;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.UserRepository;
import ru.smirnov.brewery.service.BatchService;
import ru.smirnov.brewery.service.QualityDecisionService;
import ru.smirnov.brewery.service.QualityLogService;
import ru.smirnov.brewery.service.ReportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/technologist")
public class TechnologistController {
    
    private final BatchService batchService;
    private final QualityDecisionService decisionService;
    private final QualityLogService logService;
    private final ReportService reportService;
    private final UserRepository userRepository;
    
    @Autowired
    public TechnologistController(BatchService batchService, QualityDecisionService decisionService,
                                 QualityLogService logService, ReportService reportService,
                                 UserRepository userRepository) {
        this.batchService = batchService;
        this.decisionService = decisionService;
        this.logService = logService;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Batch> batches = batchService.getAllBatches();
        model.addAttribute("batches", batches);
        return "technologist/dashboard";
    }
    
    @GetMapping("/batches")
    public String batches(Model model) {
        List<Batch> batches = batchService.getAllBatches();
        model.addAttribute("batches", batches);
        return "technologist/batches";
    }
    
    @GetMapping("/batches/create")
    public String showCreateBatchForm(Model model) {
        model.addAttribute("batch", new Batch());
        return "technologist/create-batch";
    }
    
    @PostMapping("/batches/create")
    public String createBatch(@RequestParam String batchNumber,
                             @RequestParam String productType,
                             Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElse(null);
        
        // Проверка на уникальность номера партии
        if (batchService.getBatchByNumber(batchNumber).isPresent()) {
            return "redirect:/technologist/batches/create?error=duplicate";
        }
        
        batchService.createBatch(batchNumber, productType, 
                                user != null ? user.getUserId() : null);
        return "redirect:/technologist/batches?created=true";
    }
    
    @GetMapping("/batches/{id}/decide")
    public String showDecisionForm(@PathVariable Integer id, Model model) {
        Batch batch = batchService.getBatchById(id)
            .orElseThrow(() -> new RuntimeException("Batch not found: " + id));
        List<QualityDecision> decisions = decisionService.getDecisionsByBatchId(id);
        model.addAttribute("batch", batch);
        model.addAttribute("decisions", decisions);
        model.addAttribute("decision", new QualityDecision());
        return "technologist/make-decision";
    }
    
    @PostMapping("/batches/{id}/approve")
    public String approveBatch(@PathVariable Integer id,
                              @RequestParam(required = false) String comment,
                              Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElse(null);
        
        decisionService.approveBatch(id, comment, user != null ? user.getUserId() : null);
        return "redirect:/technologist/batches?approved=true";
    }
    
    @PostMapping("/batches/{id}/reject")
    public String rejectBatch(@PathVariable Integer id,
                             @RequestParam(required = false) String comment,
                             Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElse(null);
        
        decisionService.rejectBatch(id, comment, user != null ? user.getUserId() : null);
        return "redirect:/technologist/batches?rejected=true";
    }
    
    @GetMapping("/reports")
    public String reports() {
        return "technologist/reports";
    }
    
    @GetMapping("/reports/quality-dynamics")
    public String qualityDynamicsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {
        Map<String, Object> report = reportService.generateQualityDynamicsReport(startDate, endDate);
        
        if (report.containsKey("error")) {
            model.addAttribute("error", report.get("error"));
            return "technologist/reports";
        }
        
        model.addAttribute("report", report);
        return "technologist/quality-dynamics-report";
    }
    
    @GetMapping("/reports/deviations")
    public String deviationsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {
        Map<String, Object> report = reportService.generateDeviationsReport(startDate, endDate);
        model.addAttribute("report", report);
        return "technologist/deviations-report";
    }
    
    @GetMapping("/logs")
    public String logs(Model model) {
        List<QualityLog> logs = logService.getAllLogs();
        model.addAttribute("logs", logs);
        return "technologist/logs";
    }
}

