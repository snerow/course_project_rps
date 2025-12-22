package ru.smirnov.brewery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.smirnov.brewery.entity.ProductionStage;
import ru.smirnov.brewery.entity.User;
import ru.smirnov.brewery.repository.ProductionStageRepository;
import ru.smirnov.brewery.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final ProductionStageRepository stageRepository;
    
    @Autowired
    public AdminController(UserService userService, ProductionStageRepository stageRepository) {
        this.userService = userService;
        this.stageRepository = stageRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create-user";
    }
    
    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String fullName,
                            @RequestParam String roleName) {
        userService.createUser(username, password, fullName, roleName);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        return "admin/edit-user";
    }
    
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Integer id,
                            @RequestParam(required = false) String fullName,
                            @RequestParam(required = false) String roleName) {
        userService.updateUser(id, fullName, roleName);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/stages")
    public String stages(Model model) {
        List<ProductionStage> stages = stageRepository.findAll();
        model.addAttribute("stages", stages);
        return "admin/stages";
    }
    
    @GetMapping("/stages/{id}/edit")
    public String showEditStageForm(@PathVariable Integer id, Model model) {
        ProductionStage stage = stageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Stage not found: " + id));
        model.addAttribute("stage", stage);
        return "admin/edit-stage";
    }
    
    @PostMapping("/stages/{id}/edit")
    public String updateStage(@PathVariable Integer id,
                             @RequestParam(required = false) Double normsPHMin,
                             @RequestParam(required = false) Double normsPHMax,
                             @RequestParam(required = false) Double normsDensityMin,
                             @RequestParam(required = false) Double normsDensityMax,
                             @RequestParam(required = false) Double normsAlcoholMin,
                             @RequestParam(required = false) Double normsAlcoholMax) {
        ProductionStage stage = stageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Stage not found: " + id));
        
        if (normsPHMin != null) stage.setNormsPHMin(normsPHMin);
        if (normsPHMax != null) stage.setNormsPHMax(normsPHMax);
        if (normsDensityMin != null) stage.setNormsDensityMin(normsDensityMin);
        if (normsDensityMax != null) stage.setNormsDensityMax(normsDensityMax);
        if (normsAlcoholMin != null) stage.setNormsAlcoholMin(normsAlcoholMin);
        if (normsAlcoholMax != null) stage.setNormsAlcoholMax(normsAlcoholMax);
        
        stageRepository.save(stage);
        return "redirect:/admin/stages";
    }
}

