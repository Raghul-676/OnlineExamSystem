package com.seceorg.onlineexam.online_exam_system.repository;

import com.seceorg.onlineexam.online_exam_system.model.Feedback;
import com.seceorg.onlineexam.online_exam_system.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByResult(Result result);
    List<Feedback> findByResultExamId(Long examId);
}