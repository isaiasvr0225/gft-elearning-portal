package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledCourseDTO {
    private Long id;
    private String title;
    private String description;
    private Double progressPercentage;
    private Boolean isCompleted;
}
