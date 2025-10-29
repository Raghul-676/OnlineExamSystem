package com.seceorg.onlineexam.online_exam_system.service;

import com.seceorg.onlineexam.online_exam_system.model.Question;
import com.seceorg.onlineexam.online_exam_system.model.User;
import com.seceorg.onlineexam.online_exam_system.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    
    public List<Question> getQuestionsBySubject(String subject) {
        return questionRepository.findBySubject(subject);
    }
    
    public List<Question> getQuestionsByCreator(User creator) {
        return questionRepository.findByCreatedBy(creator);
    }
    
    public List<Question> getQuestionsByType(Question.QuestionType type) {
        return questionRepository.findByQuestionType(type);
    }
    
    public List<Question> getQuestionsByDifficulty(Question.DifficultyLevel difficulty) {
        return questionRepository.findByDifficultyLevel(difficulty);
    }
    
    @Transactional
    public Question createQuestion(Question question) {
        question.setCreatedAt(LocalDateTime.now());
        return questionRepository.save(question);
    }
    
    @Transactional
    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }
    
    public boolean validateQuestion(Question question) {
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            return false;
        }
        
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
            return question.getOptionA() != null && question.getOptionB() != null &&
                   question.getOptionC() != null && question.getOptionD() != null &&
                   question.getCorrectAnswer() != null;
        }
        
        return question.getCorrectAnswer() != null && !question.getCorrectAnswer().trim().isEmpty();
    }
}