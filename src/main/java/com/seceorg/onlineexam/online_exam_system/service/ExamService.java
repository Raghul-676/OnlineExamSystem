package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.*;
import com.seceorg.onlineexam.online_exam_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamQueestionRepository examQuestionRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ResultRepository resultRepository;
    
    public List<Exam> getAllActiveExams() {
        return examRepository.findByIsActiveTrue();
    }
    
    public List<Exam> getActiveExams() {
        return examRepository.findActiveExams(LocalDateTime.now());
    }
    
    public List<Exam> getUpcomingExams() {
        return examRepository.findUpcomingExams(LocalDateTime.now());
    }
    
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }
    
    public List<Exam> getExamsByCreator(User creator) {
        return examRepository.findByCreatedBy(creator);
    }
    
    @Transactional
    public Exam createExam(Exam exam) {
        exam.setCreatedAt(LocalDateTime.now());
        return examRepository.save(exam);
    }
    
    @Transactional
    public Exam updateExam(Exam exam) {
        return examRepository.save(exam);
    }
    
    @Transactional
    public void deleteExam(Long examId) {
        examRepository.deleteById(examId);
    }
    
    public List<ExamQuestion> getExamQuestions(Long examId) {
        Optional<Exam> exam = examRepository.findById(examId);
        if (exam.isPresent()) {
            return examQuestionRepository.findByExamOrderByQuestionOrder(exam.get());
        }
        return List.of();
    }
    
    @Transactional
    public void addQuestionToExam(Long examId, Long questionId, Integer order, Integer marks) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        
        if (examOpt.isPresent() && questionOpt.isPresent()) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExam(examOpt.get());
            examQuestion.setQuestion(questionOpt.get());
            examQuestion.setQuestionOrder(order);
            examQuestion.setMarks(marks);
            examQuestionRepository.save(examQuestion);
        }
    }
    
    public boolean canTakeExam(Long examId, User student) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isEmpty()) return false;
        
        Exam exam = examOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Check if exam is active and within time bounds
        if (!exam.getIsActive() || now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            return false;
        }
        
        // Check if student has already taken the exam
        Optional<Result> existingResult = resultRepository.findByExamAndStudent(exam, student);
        return existingResult.isEmpty();
    }
    
    public boolean isExamActive(Long examId) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isEmpty()) return false;
        
        Exam exam = examOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        return exam.getIsActive() && 
               !now.isBefore(exam.getStartTime()) && 
               !now.isAfter(exam.getEndTime());
    }
}