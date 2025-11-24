package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private CategoryDTO category;
    private List<ModuleResponseDTO> modules;
}
