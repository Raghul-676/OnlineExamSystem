package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.Feedback;
import com.seceorg.onlineexam.online_exam_system.model.Result;
import com.seceorg.onlineexam.online_exam_system.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
    
    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }
    
    public List<Feedback> getFeedbackByResult(Result result) {
        return feedbackRepository.findByResult(result);
    }
    
    public List<Feedback> getFeedbackByExam(Long examId) {
        return feedbackRepository.findByResultExamId(examId);
    }
    
    @Transactional
    public Feedback createFeedback(Feedback feedback) {
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }
    
    @Transactional
    public Feedback updateFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
    
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }
}