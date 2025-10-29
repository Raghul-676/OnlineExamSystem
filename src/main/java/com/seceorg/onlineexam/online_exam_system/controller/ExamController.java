package com.seceorg.onlineexam.online_exam_system.controller;

import com.seceorg.onlineexam.online_exam_system.model.*;
import com.seceorg.onlineexam.online_exam_system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Controller
@RequestMapping("/exams")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping
    public String listExams(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        String userRole = (String) session.getAttribute("userRole");
        
        if ("STUDENT".equals(userRole)) {
            model.addAttribute("activeExams", examService.getActiveExams());
            model.addAttribute("upcomingExams", examService.getUpcomingExams());
        } else {
            model.addAttribute("exams", examService.getExamsByCreator(currentUser));
        }
        
        model.addAttribute("userRole", userRole);
        return "exams/list";
    }
    
    @GetMapping("/create")
    public String createExamForm(Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("exam", new Exam());
        return "exams/create";
    }
    
    @PostMapping("/create")
    public String createExam(@ModelAttribute Exam exam, 
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        exam.setCreatedBy(currentUser);
        
        Exam savedExam = examService.createExam(exam);
        redirectAttributes.addFlashAttribute("success", "Exam created successfully!");
        return "redirect:/exams/" + savedExam.getId();
    }
    
    @GetMapping("/{id}")
    public String viewExam(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/auth/login";
        }
        
        Optional<Exam> examOpt = examService.getExamById(id);
        if (examOpt.isEmpty()) {
            return "redirect:/exams";
        }
        
        Exam exam = examOpt.get();
        User currentUser = authService.getCurrentUser(session);
        String userRole = (String) session.getAttribute("userRole");
        
        model.addAttribute("exam", exam);
        model.addAttribute("userRole", userRole);
        
        if ("STUDENT".equals(userRole)) {
            model.addAttribute("canTake", examService.canTakeExam(id, currentUser));
            Optional<Result> result = resultService.getStudentExamResult(id, currentUser);
            model.addAttribute("hasResult", result.isPresent());
            if (result.isPresent()) {
                model.addAttribute("result", result.get());
            }
        } else {
            List<ExamQuestion> questions = examService.getExamQuestions(id);
            model.addAttribute("questions", questions);
            model.addAttribute("results", resultService.getResultsByExam(exam));
        }
        
        return "exams/view";
    }
    
    @GetMapping("/{id}/take")
    public String takeExam(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "STUDENT")) {
            return "redirect:/auth/login";
        }
        
        User currentUser = authService.getCurrentUser(session);
        
        if (!examService.canTakeExam(id, currentUser)) {
            return "redirect:/exams/" + id;
        }
        
        Result result = resultService.startExam(id, currentUser);
        if (result == null) {
            return "redirect:/exams/" + id;
        }
        
        List<ExamQuestion> questions = examService.getExamQuestions(id);
        model.addAttribute("result", result);
        model.addAttribute("exam", result.getExam());
        model.addAttribute("questions", questions);
        
        return "exams/take";
    }
    
    @PostMapping("/{resultId}/submit")
    public String submitExam(@PathVariable Long resultId,
                           @RequestParam Map<String, String> answers,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "STUDENT")) {
            return "redirect:/auth/login";
        }
        
        // Remove non-answer parameters
        answers.remove("_csrf");
        
        Result result = resultService.submitExam(resultId, answers);
        if (result != null) {
            redirectAttributes.addFlashAttribute("success", "Exam submitted successfully!");
            return "redirect:/results/" + result.getId();
        }
        
        redirectAttributes.addFlashAttribute("error", "Error submitting exam");
        return "redirect:/exams";
    }
    
    @GetMapping("/{id}/questions")
    public String manageQuestions(@PathVariable Long id, Model model, HttpSession session) {
        if (!authService.isLoggedIn(session) || !authService.hasRole(session, "TEACHER")) {
            return "redirect:/auth/login";
        }
        
        Optional<Exam> examOpt = examService.getExamById(id);
        if (examOpt.isEmpty()) {
            return "redirect:/exams";
        }
        
        model.addAttribute("exam", examOpt.get());
        model.addAttribute("examQuestions", examService.getExamQuestions(id));
        model.addAttribute("availableQuestions", questionService.getAllQuestions());
        
        return "exams/questions";
    }
    
    @PostMapping("/{examId}/questions/{questionId}")
    public String addQuestionToExam(@PathVariable Long examId,
                                  @PathVariable Long questionId,
                                  @RequestParam Integer order,
                                  @RequestParam Integer marks,
                                  RedirectAttributes redirectAttributes) {
        examService.addQuestionToExam(examId, questionId, order, marks);
        redirectAttributes.addFlashAttribute("success", "Question added to exam!");
        return "redirect:/exams/" + examId + "/questions";
    }
}