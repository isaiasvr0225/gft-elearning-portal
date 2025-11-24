package com.gft.infrastructure.repositories;

import com.gft.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.name = :roleName")
    Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);

    Optional<User> findUserByPhoneNumber(@NotNull @NotBlank String phoneNumber);

    Optional<User> findUserByEmail(@NotNull @NotBlank @Email String email);
    
    Optional<User> findUserByEmailOrPhoneNumber(@NotNull @NotBlank @Email String email, @NotNull @NotBlank String phoneNumber);

    @Query("SELECT u.phoneNumber FROM User u WHERE u.documentNumber = :documentNumber")
    String findUserPhoneNumberById(@Param("documentNumber") Long documentNumber);

    Optional<User> findUserByDocumentNumber(Long documentNumber);

    @Query("SELECT r.name FROM User u JOIN u.role r WHERE u.documentNumber = :documentNumber")
    String findUserLoggedRoleById(@Param("documentNumber") Long documentNumber);

    @Query("SELECT r.name FROM User u JOIN u.role r WHERE u.email = :email")
    String findUserLoggedRoleByEmail(@Param("email") String email);

    boolean existsByPhoneNumber(@NotNull @NotBlank String phoneNumber);

    boolean existsByEmail(@NotNull @NotBlank @Email String email);

    boolean existsByDocumentNumber(Long documentNumber);

    Optional<User> findByEmail(String email);
}