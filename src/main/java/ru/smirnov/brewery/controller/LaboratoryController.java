package ru.smirnov.brewery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.smirnov.brewery.entity.Analysis;
import ru.smirnov.brewery.entity.Batch;
import ru.smirnov.brewery.entity.ProductionStage;
import ru.smirnov.brewery.entity.Sample;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.ProductionStageRepository;
import ru.smirnov.brewery.repository.UserRepository;
import ru.smirnov.brewery.service.AnalysisService;
import ru.smirnov.brewery.service.BatchService;
import ru.smirnov.brewery.service.SampleService;

import java.util.List;

@Controller
@RequestMapping("/laboratory")
public class LaboratoryController {
    
    private final BatchService batchService;
    private final SampleService sampleService;
    private final AnalysisService analysisService;
    private final ProductionStageRepository stageRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public LaboratoryController(BatchService batchService, SampleService sampleService,
                               AnalysisService analysisService, ProductionStageRepository stageRepository,
                               UserRepository userRepository) {
        this.batchService = batchService;
        this.sampleService = sampleService;
        this.analysisService = analysisService;
        this.stageRepository = stageRepository;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Batch> batches = batchService.getAllBatches();
        List<Sample> samples = sampleService.getAllSamples();
        model.addAttribute("batches", batches);
        model.addAttribute("samples", samples);
        return "laboratory/dashboard";
    }
    
    @GetMapping("/samples")
    public String samples(Model model) {
        List<Sample> samples = sampleService.getAllSamples();
        model.addAttribute("samples", samples);
        return "laboratory/samples";
    }
    
    @GetMapping("/samples/register")
    public String showRegisterSampleForm(Model model) {
        List<Batch> batches = batchService.getAllBatches();
        List<ProductionStage> stages = stageRepository.findAll();
        model.addAttribute("batches", batches);
        model.addAttribute("stages", stages);
        model.addAttribute("sample", new Sample());
        return "laboratory/register-sample";
    }
    
    @PostMapping("/samples/register")
    public String registerSample(@RequestParam Integer batchId,
                                @RequestParam Integer stageId,
                                Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElse(null);
        
        sampleService.createSample(batchId, stageId, user != null ? user.getUserId() : null);
        return "redirect:/laboratory/samples";
    }
    
    @GetMapping("/samples/{id}/analyze")
    public String showAnalyzeForm(@PathVariable Integer id, Model model) {
        Sample sample = sampleService.getSampleById(id)
            .orElseThrow(() -> new RuntimeException("Sample not found: " + id));
        model.addAttribute("sample", sample);
        model.addAttribute("analysis", new Analysis());
        return "laboratory/analyze-sample";
    }
    
    @PostMapping("/samples/{id}/analyze")
    public String saveAnalysis(@PathVariable Integer id,
                              @RequestParam String analysisType,
                              @RequestParam(required = false) Double pH,
                              @RequestParam(required = false) Double density,
                              @RequestParam(required = false) Double alcoholContent,
                              Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElse(null);
        
        Analysis analysis = analysisService.createAnalysis(
            id, analysisType, pH, density, alcoholContent,
            user != null ? user.getUserId() : null
        );
        
        if (analysis.getWithinNorms()) {
            return "redirect:/laboratory/samples?success=true";
        } else {
            return "redirect:/laboratory/samples?warning=true";
        }
    }
}

