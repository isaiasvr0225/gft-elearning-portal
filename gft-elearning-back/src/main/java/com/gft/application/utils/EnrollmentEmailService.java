package com.gft.application.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EnrollmentEmailService {

    private final MailSenderService mailSenderService;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.frontend.start-course-url:#}")
    private String defaultStartCourseUrl;

    @Value("${app.help.center-url:#}")
    private String defaultHelpCenterUrl;

    @Async("asyncExecutor")
    public void sendCourseAssignedEmail(
            String email,
            String userName,
            String courseTitle,
            String startLink,
            String helpLink,
            String duration,
            String dueDate
    ) {
        String subject = "🚀 Acción Requerida: Se te ha asignado el curso \"" + courseTitle + "\"";

        Context context = new Context();
        context.setVariable("name", userName);
        context.setVariable("courseTitle", courseTitle);
        context.setVariable("duration", duration == null ? "" : duration);
        context.setVariable("dueDate", dueDate == null ? "" : dueDate);
        context.setVariable("startLink", (startLink == null || startLink.isBlank()) ? defaultStartCourseUrl : startLink);
        context.setVariable("helpLink", (helpLink == null || helpLink.isBlank()) ? defaultHelpCenterUrl : helpLink);
        context.setVariable("year", LocalDate.now().getYear());

        String htmlContent = templateEngine.process("course-assigned-email", context);

        mailSenderService.sendHtmlMessage(
                email,
                subject,
                htmlContent
        );
    }
}
