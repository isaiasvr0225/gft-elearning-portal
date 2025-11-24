package com.gft.application.course;

import com.gft.domain.Category;
import com.gft.domain.Course;
import com.gft.domain.CourseModule;
import com.gft.infrastructure.repositories.CategoryRepository;
import com.gft.infrastructure.repositories.CourseModuleRepository;
import com.gft.infrastructure.repositories.CourseRepository;
import com.gft.infrastructure.repositories.ModuleCompletionRepository;
import com.gft.infrastructure.repositories.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseManagementService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final UserCourseRepository userCourseRepository;
    private final ModuleCompletionRepository moduleCompletionRepository;

    @Transactional
    public Course createCourse(String title, String description, Long categoryId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título del curso es obligatorio");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Course course = Course.builder()
                .title(title.trim())
                .description(description)
                .category(category)
                .build();
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, String title, String description, Long categoryId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        if (title != null) {
            if (title.isBlank()) {
                throw new IllegalArgumentException("El título no puede estar vacío");
            }
            course.setTitle(title.trim());
        }
        if (description != null) {
            course.setDescription(description);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            course.setCategory(category);
        }
        return courseRepository.save(course);
    }

    @Transactional
    public List<CourseModule> addModules(Long courseId, List<ModuleDraft> drafts) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        if (drafts == null || drafts.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un módulo");
        }
        long countExisting = moduleRepository.countByCourseId(courseId);
        int nextIndexBase = (int) countExisting; // zero-based
        List<CourseModule> toSave = new ArrayList<>();
        int i = 0;
        for (ModuleDraft d : drafts) {
            if (d.title == null || d.title.isBlank()) {
                throw new IllegalArgumentException("El título del módulo es obligatorio");
            }
            Integer orderIndex = d.orderIndex;
            if (orderIndex == null) {
                orderIndex = nextIndexBase + i + 1; // 1-based ordering as commonly displayed
            }
            CourseModule module = CourseModule.builder()
                    .course(course)
                    .title(d.title.trim())
                    .content(d.content)
                    .orderIndex(orderIndex)
                    .build();
            toSave.add(module);
            i++;
        }
        return moduleRepository.saveAll(toSave);
    }

    @Transactional(readOnly = true)
    public List<Course> listAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<CourseModule> listModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    @Transactional(readOnly = true)
    public CourseModule getModuleOrThrow(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        // ensure course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        // delete module completions for all modules of this course
        moduleCompletionRepository.deleteByModuleCourseId(course.getId());
        // delete enrollments for this course
        userCourseRepository.deleteByCourseId(course.getId());
        // delete course (will cascade delete modules due to orphanRemoval)
        courseRepository.delete(course);
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        // delete completions for this module
        moduleCompletionRepository.deleteByModuleId(module.getId());
        // delete module itself
        moduleRepository.delete(module);
    }

    public record ModuleDraft(String title, String content, Integer orderIndex) {}
}
