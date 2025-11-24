package com.gft.infrastructure.repositories;

import com.gft.domain.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    Optional<UserCourse> findByUserDocumentNumberAndCourseId(Long documentNumber, Long courseId);
    List<UserCourse> findByUserDocumentNumber(Long documentNumber);
    // Filter user enrollments by category name (case-insensitive)
    List<UserCourse> findByUserDocumentNumberAndCourseCategoryNameIgnoreCase(Long documentNumber, String categoryName);
    void deleteByCourseId(Long courseId);
}
