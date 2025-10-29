package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.Question;
import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.service.QuestionService;
import com.seceorg.onlineexam.online_exam_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping
    public String listQuestions(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        List<Question> questions = questionService.getQuestionsByCreator(currentUser);
        
        model.addAttribute("questions", questions);
        return "questions/list";
    }
    
    @GetMapping("/create")
    public String createQuestionForm(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("question", new Question());
        model.addAttribute("questionTypes", Question.QuestionType.values());
        model.addAttribute("difficultyLevels", Question.DifficultyLevel.values());
        
        return "questions/create";
    }
    
    @PostMapping("/create")
    public String createQuestion(@ModelAttribute Question question,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        question.setCreatedBy(currentUser);
        
        if (!questionService.validateQuestion(question)) {
            redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly");
            return "redirect:/questions/create";
        }
        
        questionService.createQuestion(question);
        redirectAttributes.addFlashAttribute("success", "Question created successfully!");
        
        return "redirect:/questions";
    }
    
    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isEmpty()) {
            return "redirect:/questions";
        }
        
        model.addAttribute("question", questionOpt.get());
        return "questions/view";
    }
    
    @GetMapping("/{id}/edit")
    public String editQuestionForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isEmpty()) {
            return "redirect:/questions";
        }
        
        Question question = questionOpt.get();
        User currentUser = authService.getCurrentUser(session);
        
        // Check if teacher owns this question
        if (!question.getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/questions";
        }
        
        model.addAttribute("question", question);
        model.addAttribute("questionTypes", Question.QuestionType.values());
        model.addAttribute("difficultyLevels", Question.DifficultyLevel.values());
        
        return "questions/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updateQuestion(@PathVariable Long id,
                               @ModelAttribute Question question,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Question> existingQuestionOpt = questionService.getQuestionById(id);
        if (existingQuestionOpt.isEmpty()) {
            return "redirect:/questions";
        }
        
        Question existingQuestion = existingQuestionOpt.get();
        User currentUser = authService.getCurrentUser(session);
        
        // Check if teacher owns this question
        if (!existingQuestion.getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/questions";
        }
        
        question.setId(id);
        question.setCreatedBy(currentUser);
        question.setCreatedAt(existingQuestion.getCreatedAt());
        
        if (!questionService.validateQuestion(question)) {
            redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly");
            return "redirect:/questions/" + id + "/edit";
        }
        
        questionService.updateQuestion(question);
        redirectAttributes.addFlashAttribute("success", "Question updated successfully!");
        
        return "redirect:/questions/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            User currentUser = authService.getCurrentUser(session);
            
            // Check if teacher owns this question
            if (question.getCreatedBy().getId().equals(currentUser.getId())) {
                questionService.deleteQuestion(id);
                redirectAttributes.addFlashAttribute("success", "Question deleted successfully!");
            }
        }
        
        return "redirect:/questions";
    }
}