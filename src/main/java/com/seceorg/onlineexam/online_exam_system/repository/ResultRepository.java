package com.seceorg.onlineexam.online_exam_system.repository;

import com.seceorg.onlineexam.online_exam_system.model.Result;
import com.seceorg.onlineexam.online_exam_system.model.Exam;
import com.seceorg.onlineexam.online_exam_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByStudent(User student);
    List<Result> findByExam(Exam exam);
    Optional<Result> findByExamAndStudent(Exam exam, User student);
    
    @Query("SELECT r FROM Result r WHERE r.exam = :exam ORDER BY r.score DESC")
    List<Result> findByExamOrderByScoreDesc(Exam exam);
    
    @Query("SELECT AVG(r.percentage) FROM Result r WHERE r.exam = :exam")
    Double findAveragePercentageByExam(Exam exam);
}