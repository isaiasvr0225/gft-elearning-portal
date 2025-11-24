package com.gft.infrastructure.repositories;

import com.gft.domain.ModuleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleCompletionRepository extends JpaRepository<ModuleCompletion, Long> {
    long countByUserDocumentNumberAndModuleCourseId(Long documentNumber, Long courseId);
    boolean existsByUserDocumentNumberAndModuleId(Long documentNumber, Long moduleId);
    long deleteByModuleCourseId(Long courseId);
    long deleteByModuleId(Long moduleId);
}
