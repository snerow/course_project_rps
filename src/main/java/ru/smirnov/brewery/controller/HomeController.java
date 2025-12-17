package ru.smirnov.brewery.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("role", role);
            
            if (role.contains("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.contains("TECHNOLOGIST")) {
                return "redirect:/technologist/dashboard";
            } else if (role.contains("LABORATORY_ASSISTANT")) {
                return "redirect:/laboratory/dashboard";
            }
        }
        return "redirect:/login";
    }
}

