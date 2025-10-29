package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.*;
import com.seceorg.onlineexam.online_exam_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Controller
@RequestMapping("/results")
public class ResultController {
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ExamService examService;
    
    @GetMapping
    public String listResults(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        String userRole = (String) session.getAttribute("userRole");
        
        if ("STUDENT".equals(userRole)) {
            List<Result> results = resultService.getResultsByStudent(currentUser);
            model.addAttribute("results", results);
        } else {
            // For teachers, show results of their exams
            List<Exam> teacherExams = examService.getExamsByCreator(currentUser);
            model.addAttribute("exams", teacherExams);
        }
        
        model.addAttribute("userRole", userRole);
        return "results/list";
    }
    
    @GetMapping("/{id}")
    public String viewResult(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<Result> resultOpt = resultService.getResult(id);
        if (resultOpt.isEmpty()) {
            return "redirect:/results";
        }
        
        Result result = resultOpt.get();
        User currentUser = authService.getCurrentUser(session);
        String userRole = (String) session.getAttribute("userRole");
        
        // Check access permissions
        if ("STUDENT".equals(userRole) && !result.getStudent().getId().equals(currentUser.getId())) {
            return "redirect:/results";
        }
        
        if ("TEACHER".equals(userRole) && !result.getExam().getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/results";
        }
        
        model.addAttribute("result", result);
        model.addAttribute("userRole", userRole);
        
        // Get student answers and exam questions for detailed view
        Map<String, String> studentAnswers = resultService.getStudentAnswers(result);
        List<ExamQuestion> examQuestions = examService.getExamQuestions(result.getExam().getId());
        
        model.addAttribute("studentAnswers", studentAnswers);
        model.addAttribute("examQuestions", examQuestions);
        
        return "results/view";
    }
    
    @GetMapping("/exam/{examId}")
    public String examResults(@PathVariable Long examId, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isEmpty()) {
            return "redirect:/results";
        }
        
        Exam exam = examOpt.get();
        User currentUser = authService.getCurrentUser(session);
        
        // Check if teacher owns this exam
        if (!exam.getCreatedBy().getId().equals(currentUser.getId())) {
            return "redirect:/results";
        }
        
        List<Result> results = resultService.getResultsByExam(exam);
        List<Result> leaderboard = resultService.getExamLeaderboard(examId);
        Double averageScore = resultService.getExamAverageScore(examId);
        
        model.addAttribute("exam", exam);
        model.addAttribute("results", results);
        model.addAttribute("leaderboard", leaderboard);
        model.addAttribute("averageScore", averageScore != null ? averageScore : 0.0);
        
        return "results/exam-results";
    }
}