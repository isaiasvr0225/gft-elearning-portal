package com.gft.infrastructure.dto;

import com.gft.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Builder
public record LoginUsingNITDTO(

        @NotNull
        Long nit,

        @NotNull
        @Pattern(message = "La contraseña debe tener al menos 8 caracteres y contener al menos un número.", regexp = "^(?=.*\\d).{8,}$")
        @NotBlank
        String password

) implements Serializable {}