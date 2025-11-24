package com.gft.infrastructure.repositories;

import com.gft.domain.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    long countByCourseId(Long courseId);
}
