package com.gft.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "course_modules")
public class CourseModule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // Puede ser texto plano o HTML

    @Column(name = "order_index")
    private Integer orderIndex;
}
