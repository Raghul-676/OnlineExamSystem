package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.Feedback;
import com.seceorg.onlineexam.online_exam_system.model.Result;
import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.service.FeedbackService;
import com.seceorg.onlineexam.online_exam_system.service.ResultService;
import com.seceorg.onlineexam.online_exam_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/result/{resultId}")
    public String createFeedbackForm(@PathVariable Long resultId, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Result> resultOpt = resultService.getResult(resultId);
        if (resultOpt.isEmpty()) {
            return "redirect:/results";
        }
        
        Result result = resultOpt.get();
        User currentUser = authService.getCurrentUser(session);
        
        // Check if teacher owns the exam
        if (!result.getExam().getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/results";
        }
        
        model.addAttribute("result", result);
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("existingFeedback", feedbackService.getFeedbackByResult(result));
        
        return "feedback/create";
    }
    
    @PostMapping("/result/{resultId}")
    public String createFeedback(@PathVariable Long resultId,
                               @ModelAttribute Feedback feedback,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Result> resultOpt = resultService.getResult(resultId);
        if (resultOpt.isEmpty()) {
            return "redirect:/results";
        }
        
        Result result = resultOpt.get();
        User currentUser = authService.getCurrentUser(session);
        
        // Check if teacher owns the exam
        if (!result.getExam().getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/results";
        }
        
        feedback.setResult(result);
        feedback.setCreatedBy(currentUser);
        
        feedbackService.createFeedback(feedback);
        redirectAttributes.addFlashAttribute("success", "Feedback added successfully!");
        
        return "redirect:/results/" + resultId;
    }
}