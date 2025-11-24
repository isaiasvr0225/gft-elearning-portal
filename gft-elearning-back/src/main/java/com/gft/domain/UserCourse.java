package com.gft.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_courses", uniqueConstraints = @UniqueConstraint(columnNames = {"user_document_number", "course_id"}))
public class UserCourse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_document_number", referencedColumnName = "document_number", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Builder.Default
    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage = 0.0; // 0 a 100

    @Builder.Default
    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();
}
