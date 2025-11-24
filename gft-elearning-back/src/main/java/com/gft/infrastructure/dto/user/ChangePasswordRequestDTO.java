package com.gft.infrastructure.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangePasswordRequestDTO(
        @NotBlank
        String currentPassword,

        @NotBlank
        String newPassword
) {}
