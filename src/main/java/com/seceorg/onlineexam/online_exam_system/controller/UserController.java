package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.service.AuthService;
import com.seceorg.onlineexam.online_exam_system.service.ExamService;
import com.seceorg.onlineexam.online_exam_system.service.ResultService;
import com.seceorg.onlineexam.online_exam_system.service.UserService;
import com.seceorg.onlineexam.online_exam_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home(HttpSession session) {
        if (authService.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        return "redirect:/auth/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        String userRole = (String) session.getAttribute("userRole");
        
        model.addAttribute("user", currentUser);
        model.addAttribute("userRole", userRole);
        
        if ("STUDENT".equals(userRole)) {
            model.addAttribute("activeExams", examService.getActiveExams());
            model.addAttribute("upcomingExams", examService.getUpcomingExams());
            model.addAttribute("recentResults", resultService.getResultsByStudent(currentUser));
        } else if ("TEACHER".equals(userRole)) {
            model.addAttribute("myExams", examService.getExamsByCreator(currentUser));
        }
        
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        model.addAttribute("user", currentUser);
        
        return "profile";
    }
}