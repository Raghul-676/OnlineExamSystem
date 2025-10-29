package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.*;
import com.seceorg.onlineexam.online_exam_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ExamQueestionRepository examQuestionRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Transactional
    public Result startExam(Long examId, User student) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isEmpty()) return null;
        
        Exam exam = examOpt.get();
        
        // Check if student already has a result for this exam
        Optional<Result> existingResult = resultRepository.findByExamAndStudent(exam, student);
        if (existingResult.isPresent()) {
            return existingResult.get();
        }
        
        Result result = new Result();
        result.setExam(exam);
        result.setStudent(student);
        result.setStartTime(LocalDateTime.now());
        result.setTotalQuestions(examQuestionRepository.findByExam(exam).size());
        
        return resultRepository.save(result);
    }
    
    @Transactional
    public Result submitExam(Long resultId, Map<String, String> answers) {
        Optional<Result> resultOpt = resultRepository.findById(resultId);
        if (resultOpt.isEmpty()) return null;
        
        Result result = resultOpt.get();
        result.setEndTime(LocalDateTime.now());
        result.setSubmittedAt(LocalDateTime.now());
        
        // Calculate time taken
        long minutes = ChronoUnit.MINUTES.between(result.getStartTime(), result.getEndTime());
        result.setTimeTakenMinutes((int) minutes);
        
        // Store answers as JSON
        try {
            result.setAnswers(objectMapper.writeValueAsString(answers));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Auto-grade the exam
        gradeExam(result, answers);
        
        return resultRepository.save(result);
    }
    
    private void gradeExam(Result result, Map<String, String> answers) {
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(result.getExam());
        int totalScore = 0;
        int correctAnswers = 0;
        int totalMarks = 0;
        
        for (ExamQuestion examQuestion : examQuestions) {
            Question question = examQuestion.getQuestion();
            String studentAnswer = answers.get(question.getId().toString());
            totalMarks += examQuestion.getMarks();
            
            if (studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                totalScore += examQuestion.getMarks();
                correctAnswers++;
            }
        }
        
        result.setScore(totalScore);
        result.setCorrectAnswers(correctAnswers);
        
        // Calculate percentage
        if (totalMarks > 0) {
            result.setPercentage((double) totalScore / totalMarks * 100);
        }
        
        // Check if passed
        if (result.getExam().getPassingMarks() != null) {
            result.setIsPassed(totalScore >= result.getExam().getPassingMarks());
        }
    }
    
    public List<Result> getResultsByStudent(User student) {
        return resultRepository.findByStudent(student);
    }
    
    public List<Result> getResultsByExam(Exam exam) {
        return resultRepository.findByExam(exam);
    }
    
    public Optional<Result> getResult(Long resultId) {
        return resultRepository.findById(resultId);
    }
    
    public Optional<Result> getStudentExamResult(Long examId, User student) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isPresent()) {
            return resultRepository.findByExamAndStudent(examOpt.get(), student);
        }
        return Optional.empty();
    }
    
    public Map<String, String> getStudentAnswers(Result result) {
        try {
            if (result.getAnswers() != null) {
                return objectMapper.readValue(result.getAnswers(), new TypeReference<Map<String, String>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of();
    }
    
    public List<Result> getExamLeaderboard(Long examId) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isPresent()) {
            return resultRepository.findByExamOrderByScoreDesc(examOpt.get());
        }
        return List.of();
    }
    
    public Double getExamAverageScore(Long examId) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isPresent()) {
            return resultRepository.findAveragePercentageByExam(examOpt.get());
        }
        return 0.0;
    }
}