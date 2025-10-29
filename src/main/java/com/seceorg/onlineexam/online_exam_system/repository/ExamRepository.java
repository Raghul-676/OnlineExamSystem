package com.seceorg.onlineexam.online_exam_system.repository;

import com.seceorg.onlineexam.online_exam_system.model.Exam;
import com.seceorg.onlineexam.online_exam_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByIsActiveTrue();
    List<Exam> findByCreatedBy(User createdBy);
    
    @Query("SELECT e FROM Exam e WHERE e.isActive = true AND e.startTime <= :now AND e.endTime >= :now")
    List<Exam> findActiveExams(LocalDateTime now);
    
    @Query("SELECT e FROM Exam e WHERE e.isActive = true AND e.startTime > :now")
    List<Exam> findUpcomingExams(LocalDateTime now);
}