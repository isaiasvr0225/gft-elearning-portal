package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDetailResponseDTO {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private Integer orderIndex;
    private Boolean completed;
}
