package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.service.AuthService;
import com.seceorg.onlineexam.online_exam_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        User user = authService.authenticate(username, password);
        if (user != null) {
            authService.login(session, user);
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam String fullName,
                          @RequestParam(defaultValue = "STUDENT") String role,
                          RedirectAttributes redirectAttributes) {
        User user = authService.register(username, password, email, fullName, role);
        if (user != null) {
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Username or email already exists");
            return "redirect:/auth/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/auth/login";
    }
}