package com.gft.infrastructure.dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateModuleRequestDTO {
    private String title;
    private String content; // texto plano o HTML
    private Integer orderIndex; // opcional
}
