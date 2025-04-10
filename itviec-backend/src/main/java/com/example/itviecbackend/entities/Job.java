package com.example.itviecbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String department;        // Phòng ban
    private String location;          // dia diem lam viec
    private String description;       // mo ta
    private String requirements;      // yeu cau
    private String salary;

    private LocalDate postedDate;
    private LocalDate closingDate;

    //Người tạo bài đăng
    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    //Công ty đăng tuyển
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    //Người duyệt bài viết
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approver;

    //Thời gian duyệt
    private LocalDateTime approvedAt;

    //Trạng thái bài viết
    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.PENDING;

    //Lý do từ chối nếu có
    private String rejectionReason;

    //cac skill can cho job
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobSkill> jobSkills;
}
