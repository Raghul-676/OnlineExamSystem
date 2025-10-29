package com.seceorg.onlineexam.online_exam_system.repository;

import com.seceorg.onlineexam.online_exam_system.model.ExamQuestion;
import com.seceorg.onlineexam.online_exam_system.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamQueestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByExamOrderByQuestionOrder(Exam exam);
    List<ExamQuestion> findByExam(Exam exam);
    void deleteByExam(Exam exam);
}