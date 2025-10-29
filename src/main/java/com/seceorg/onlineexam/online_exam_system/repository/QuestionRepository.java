package com.seceorg.onlineexam.online_exam_system.repository;

import com.seceorg.onlineexam.online_exam_system.model.Question;
import com.seceorg.onlineexam.online_exam_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(String subject);
    List<Question> findByCreatedBy(User createdBy);
    List<Question> findByQuestionType(Question.QuestionType questionType);
    List<Question> findByDifficultyLevel(Question.DifficultyLevel difficultyLevel);
}