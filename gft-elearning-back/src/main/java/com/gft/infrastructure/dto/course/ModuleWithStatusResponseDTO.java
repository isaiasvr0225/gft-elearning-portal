package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleWithStatusResponseDTO {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Boolean completed;
}
