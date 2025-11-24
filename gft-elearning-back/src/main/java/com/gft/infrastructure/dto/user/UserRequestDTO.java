package com.gft.infrastructure.dto.user;

import com.gft.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Builder
public record UserRequestDTO(

        @NotNull
        Long documentNumber,

        @NotNull
        @NotBlank
        String documentType,

        @NotNull
        @NotBlank
        String city,

        @NotNull
        @NotBlank
        String address,

        @NotNull
        @NotBlank
        String fullName,

        @NotNull
        @NotBlank
        String phoneNumber,

        @NotNull
        @Email
        @NotBlank
        String email,

        @NotNull
        @Pattern(message = "La contraseña debe tener al menos 8 caracteres y contener al menos un número.", regexp = "^(?=.*\\d).{8,}$")
        @NotBlank
        String password,

        String profileImageLink

) implements Serializable {}