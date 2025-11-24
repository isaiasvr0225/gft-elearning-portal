package com.gft.infrastructure.dto.user;

import com.gft.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Builder
public record UserResponseDTO(

        @NotNull
        Long documentNumber,

        @NotNull
        @NotBlank
        String documentType,

        @NotNull
        @NotBlank
        String city,

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

        String role,

        String profileImageLink,

        String jwtToken

) implements Serializable {}