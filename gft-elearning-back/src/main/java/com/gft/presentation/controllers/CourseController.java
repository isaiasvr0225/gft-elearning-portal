package com.gft.presentation.controllers;

import com.gft.application.course.CourseManagementService;
import com.gft.application.course.CourseProgressService;
import com.gft.infrastructure.dto.course.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseProgressService courseProgressService;
    private final CourseManagementService courseManagementService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/courses/{courseId}/enroll")
    public ResponseEntity<Void> enrollUserByEmail(@PathVariable Long courseId, @RequestBody EnrollUserRequestDTO request) {
        courseProgressService.enrollUserInCourseByEmail(courseId, request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/modules/{moduleId}/complete")
    public ResponseEntity<CourseProgressDTO> completeModule(@PathVariable Long moduleId, Principal principal) {
        double progress = courseProgressService.completeModule(moduleId, principal);
        // Find courseId through service by moduleId would require module lookup; minimal approach: return progress only without courseId
        CourseProgressDTO dto = new CourseProgressDTO(null, progress);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<CourseProgressDTO> getProgress(@PathVariable Long courseId, Principal principal) {
        double progress = courseProgressService.getProgress(courseId, principal);
        return ResponseEntity.ok(new CourseProgressDTO(courseId, progress));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/me/courses")
    public ResponseEntity<List<EnrolledCourseDTO>> myCourses(Principal principal) {
        var list = courseProgressService.listMyCourses(principal).stream()
                .map(v -> new EnrolledCourseDTO(
                        v.courseId(),
                        v.title(),
                        v.description(),
                        v.progressPercentage(),
                        v.progressPercentage() != null && v.progressPercentage() >= 100.0
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // USER: my courses filtered by category name
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/me/courses/by-category/{categoryName}")
    public ResponseEntity<List<EnrolledCourseDTO>> myCoursesByCategory(@PathVariable String categoryName, Principal principal) {
        var list = courseProgressService.listMyCoursesByCategoryName(categoryName, principal).stream()
                .map(v -> new EnrolledCourseDTO(
                        v.courseId(),
                        v.title(),
                        v.description(),
                        v.progressPercentage(),
                        v.progressPercentage() != null && v.progressPercentage() >= 100.0
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/courses")
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody CreateCourseRequestDTO request) {
        var course = courseManagementService.createCourse(request.getTitle(), request.getDescription(), request.getCategoryId());
        var categoryDto = course.getCategory() != null ? new CategoryDTO(course.getCategory().getId(), course.getCategory().getName()) : null;
        return ResponseEntity.ok(new CourseResponseDTO(course.getId(), course.getTitle(), course.getDescription(), categoryDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleResponseDTO>> addModulesToCourse(
            @PathVariable Long courseId,
            @RequestBody List<CreateModuleRequestDTO> modules
    ) {
        var drafts = modules.stream()
                .map(m -> new CourseManagementService.ModuleDraft(m.getTitle(), m.getContent(), m.getOrderIndex()))
                .toList();
        var saved = courseManagementService.addModules(courseId, drafts);
        var response = saved.stream()
                .map(cm -> new ModuleResponseDTO(cm.getId(), cm.getTitle(), cm.getOrderIndex()))
                .toList();
        return ResponseEntity.ok(response);
    }

    // ADMIN: list all courses
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/courses")
    public ResponseEntity<List<CourseResponseDTO>> listAllCourses() {
        var courses = courseManagementService.listAllCourses();
        var response = courses.stream()
                .map(c -> new CourseResponseDTO(
                        c.getId(),
                        c.getTitle(),
                        c.getDescription(),
                        c.getCategory() != null ? new CategoryDTO(c.getCategory().getId(), c.getCategory().getName()) : null
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    // ADMIN: update course
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/courses/{courseId}")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long courseId, @RequestBody UpdateCourseRequestDTO request) {
        var course = courseManagementService.updateCourse(courseId, request.getTitle(), request.getDescription(), request.getCategoryId());
        var categoryDto = course.getCategory() != null ? new CategoryDTO(course.getCategory().getId(), course.getCategory().getName()) : null;
        return ResponseEntity.ok(new CourseResponseDTO(course.getId(), course.getTitle(), course.getDescription(), categoryDto));
    }

    // ADMIN: delete course (and its related data)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseManagementService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // ADMIN: delete a single module by id
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/modules/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        courseManagementService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    // ADMIN: course detail with modules
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/courses/{courseId}")
    public ResponseEntity<CourseDetailResponseDTO> getCourseDetail(@PathVariable Long courseId) {
        return getCourseDetailResponseDTOResponseEntity(courseId);
    }

    // USER: course detail with modules including completion status per module
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDetailWithStatusResponseDTO> getMyCourseDetail(@PathVariable Long courseId, Principal principal) {
        var course = courseManagementService.getCourseOrThrow(courseId);
        var modules = courseManagementService.listModulesByCourse(courseId).stream()
                .map(m -> new ModuleWithStatusResponseDTO(
                        m.getId(),
                        m.getTitle(),
                        m.getOrderIndex(),
                        courseProgressService.isModuleCompleted(m.getId(), principal)
                ))
                .toList();
        var categoryDto = course.getCategory() != null ? new CategoryDTO(course.getCategory().getId(), course.getCategory().getName()) : null;
        Double progress;
        try {
            progress = courseProgressService.getProgress(courseId, principal);
        } catch (RuntimeException ex) {
            // If user is not enrolled yet, return 0.0 as progress
            progress = 0.0;
        }
        var dto = new CourseDetailWithStatusResponseDTO(course.getId(), course.getTitle(), course.getDescription(), categoryDto, progress, modules);
        return ResponseEntity.ok(dto);
    }

    private ResponseEntity<CourseDetailResponseDTO> getCourseDetailResponseDTOResponseEntity(@PathVariable Long courseId) {
        var course = courseManagementService.getCourseOrThrow(courseId);
        var modules = courseManagementService.listModulesByCourse(courseId).stream()
                .map(m -> new ModuleResponseDTO(m.getId(), m.getTitle(), m.getOrderIndex()))
                .toList();
        var categoryDto = course.getCategory() != null ? new CategoryDTO(course.getCategory().getId(), course.getCategory().getName()) : null;
        var dto = new CourseDetailResponseDTO(course.getId(), course.getTitle(), course.getDescription(), categoryDto, modules);
        return ResponseEntity.ok(dto);
    }

    // USER: module detail by id
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ModuleDetailResponseDTO> getModuleDetail(@PathVariable Long moduleId, Principal principal) {
        var module = courseManagementService.getModuleOrThrow(moduleId);
        boolean completed = courseProgressService.isModuleCompleted(moduleId, principal);
        var dto = new ModuleDetailResponseDTO(
                module.getId(),
                module.getCourse().getId(),
                module.getTitle(),
                module.getContent(),
                module.getOrderIndex(),
                completed
        );
        return ResponseEntity.ok(dto);
    }
}
