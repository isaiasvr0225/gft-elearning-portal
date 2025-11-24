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
@Table(name = "module_completions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_document_number", "module_id"}))
public class ModuleCompletion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_document_number", referencedColumnName = "document_number", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @Builder.Default
    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();
}
