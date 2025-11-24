package com.gft.application.course;

import com.gft.domain.*;
import com.gft.infrastructure.repositories.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseProgressService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final UserCourseRepository userCourseRepository;
    private final ModuleCompletionRepository moduleCompletionRepository;
    private final com.gft.application.utils.EnrollmentEmailService enrollmentEmailService;

    private User resolveCurrentUser(Principal principal) {
        String identifier = principal.getName();
        return userRepository.findUserByEmailOrPhoneNumber(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public void enrollInCourse(Long courseId, Principal principal) {
        // Mantener compatibilidad si se usa este método: inscribe al usuario autenticado
        User user = resolveCurrentUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        userCourseRepository.findByUserDocumentNumberAndCourseId(user.getDocumentNumber(), courseId)
                .ifPresent(uc -> { throw new RuntimeException("Ya estás inscrito en este curso"); });

        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(course)
                .progressPercentage(0.0)
                .build();
        userCourseRepository.save(userCourse);
    }

    @Transactional
    public void enrollUserInCourseByEmail(Long courseId,  @NotBlank @NotNull String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado por email"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        userCourseRepository.findByUserDocumentNumberAndCourseId(user.getDocumentNumber(), courseId)
                .ifPresent(uc -> { throw new RuntimeException("El usuario ya está inscrito en este curso"); });

        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(course)
                .progressPercentage(0.0)
                .build();

        userCourseRepository.save(userCourse);

        // Enviar correo de asignación de curso de forma asíncrona
        try {
            enrollmentEmailService.sendCourseAssignedEmail(
                    user.getEmail(),
                    user.getFullName(),
                    course.getTitle(),
                    null, // startLink opcional, usa URL por defecto si no se provee
                    null, // helpLink opcional
                    null, // duration opcional
                    null  // dueDate opcional
            );
        } catch (Exception ignored) {
            // Evitar que un fallo en el envío de correo rompa la transacción principal
        }
    }

    @Transactional
    public double completeModule(Long moduleId, Principal principal) {
        User user = resolveCurrentUser(principal);
        CourseModule module = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        // Ensure enrollment exists
        userCourseRepository.findByUserDocumentNumberAndCourseId(user.getDocumentNumber(), module.getCourse().getId())
                .orElseGet(() -> userCourseRepository.save(UserCourse.builder()
                        .user(user)
                        .course(module.getCourse())
                        .progressPercentage(0.0)
                        .build()));

        // If not already completed, create completion record
        if (!moduleCompletionRepository.existsByUserDocumentNumberAndModuleId(user.getDocumentNumber(), moduleId)) {
            ModuleCompletion mc = ModuleCompletion.builder()
                    .user(user)
                    .module(module)
                    .build();
            moduleCompletionRepository.save(mc);
        }

        // Recalculate progress
        return updateProgress(user.getDocumentNumber(), module.getCourse().getId());
    }

    @Transactional(readOnly = true)
    public double getProgress(Long courseId, Principal principal) {
        User user = resolveCurrentUser(principal);
        UserCourse uc = userCourseRepository.findByUserDocumentNumberAndCourseId(user.getDocumentNumber(), courseId)
                .orElseThrow(() -> new RuntimeException("No estás inscrito en este curso"));
        return uc.getProgressPercentage();
    }

    @Transactional
    public double updateProgress(Long userDocumentNumber, Long courseId) {
        long totalModules = courseModuleRepository.countByCourseId(courseId);
        long completed = moduleCompletionRepository.countByUserDocumentNumberAndModuleCourseId(userDocumentNumber, courseId);

        double progress = totalModules == 0 ? 0.0 : (completed * 100.0) / totalModules;

        UserCourse uc = userCourseRepository.findByUserDocumentNumberAndCourseId(userDocumentNumber, courseId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        uc.setProgressPercentage(progress);
        userCourseRepository.save(uc);

        // Si el progreso llegó a 100%, marcar el curso como completado
        if (progress >= 100.0) {
            courseRepository.findById(courseId).ifPresent(course -> {
                if (!course.isCompleted()) {
                    course.setCompleted(true);
                    courseRepository.save(course);
                }
            });
        }
        return progress;
    }

    @Transactional(readOnly = true)
    public List<EnrolledCourseView> listMyCourses(Principal principal) {
        User user = resolveCurrentUser(principal);
        List<UserCourse> list = userCourseRepository.findByUserDocumentNumber(user.getDocumentNumber());
        return list.stream()
                .map(uc -> new EnrolledCourseView(
                        uc.getCourse().getId(),
                        uc.getCourse().getTitle(),
                        uc.getCourse().getDescription(),
                        uc.getProgressPercentage()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrolledCourseView> listMyCoursesByCategoryName(String categoryName, Principal principal) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        User user = resolveCurrentUser(principal);
        List<UserCourse> list = userCourseRepository
                .findByUserDocumentNumberAndCourseCategoryNameIgnoreCase(user.getDocumentNumber(), categoryName.trim());
        return list.stream()
                .map(uc -> new EnrolledCourseView(
                        uc.getCourse().getId(),
                        uc.getCourse().getTitle(),
                        uc.getCourse().getDescription(),
                        uc.getProgressPercentage()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isModuleCompleted(Long moduleId, Principal principal) {
        User user = resolveCurrentUser(principal);
        return moduleCompletionRepository.existsByUserDocumentNumberAndModuleId(user.getDocumentNumber(), moduleId);
    }

    public record EnrolledCourseView(Long courseId, String title, String description, Double progressPercentage) {}
}
