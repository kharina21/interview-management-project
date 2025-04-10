package com.example.itviecbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String comment;

    @ManyToOne
    private User reviewer;

    @ManyToOne
    private Company company;

    private LocalDateTime createdAt;
}