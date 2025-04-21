package com.custos.oauth.controller;

import com.custos.oauth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String confirmPassword,
            Model model) {
        
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                model.addAttribute("error", "Username is required");
                return "register";
            }
            
            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                return "register";
            }
            
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                return "register";
            }
            
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("error", "Email is required");
                return "register";
            }
            
            // Create user
            userService.createUser(username, password, email);
            return "redirect:/login?registered=true";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
} 