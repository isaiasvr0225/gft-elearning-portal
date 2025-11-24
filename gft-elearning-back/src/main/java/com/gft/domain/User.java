package com.gft.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public @Entity class User implements Serializable {

    @Id
    @Column(name = "document_number")
    private Long documentNumber;

    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;
    
    @ManyToOne
    @JoinColumn(name = "id_city")
    private City city;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;
    
    @NotNull
    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @NotBlank
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @NotNull
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String address;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*\\d).{8,}$", message = "La contraseña debe tener al menos 8 caracteres y contener al menos un número.")
    @Column(nullable = false)
    private String password;

    @Column(name = "profile_image_link")
    private String profileImageLink;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_no_expired")
    private boolean accountNoExpired;

    @Column(name = "account_no_locked")
    private boolean accountNoLocked;

    @Column(name = "credentials_no_expired")
    private boolean credentialsNoExpired;


}
