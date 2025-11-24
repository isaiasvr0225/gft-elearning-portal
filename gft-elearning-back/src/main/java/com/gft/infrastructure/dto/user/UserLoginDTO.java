package com.gft.infrastructure.dto.user;

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
public record UserLoginDTO(

        @NotNull
        @NotBlank
        String user, // can be email or phone number

        @NotNull
        @Pattern(message = "La contraseña debe tener al menos 8 caracteres y contener al menos un número.", regexp = "^(?=.*\\d).{8,}$")
        @NotBlank
        String password

) implements Serializable {}