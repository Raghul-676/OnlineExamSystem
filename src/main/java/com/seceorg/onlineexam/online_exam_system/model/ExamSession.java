package com.seceorg.onlineexam.online_exam_system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;
    
    @Column(name = "session_start")
    private LocalDateTime sessionStart;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "browser_info")
    private String browserInfo;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "violations_count")
    private Integer violationsCount = 0;
    
    @Column(name = "tab_switches")
    private Integer tabSwitches = 0;
}