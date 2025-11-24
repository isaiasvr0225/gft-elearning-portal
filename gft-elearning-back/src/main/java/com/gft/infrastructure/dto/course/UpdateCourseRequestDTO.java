package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourseRequestDTO {
    private String title;        // opcional
    private String description;  // opcional
    private Long categoryId;     // opcional
}
