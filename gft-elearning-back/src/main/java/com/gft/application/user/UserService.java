package com.gft.application.user;

import com.gft.infrastructure.dto.LoginUsingNITDTO;
import com.gft.infrastructure.dto.user.ChangePasswordRequestDTO;
import com.gft.infrastructure.dto.user.UserLoginDTO;
import com.gft.infrastructure.dto.user.UserRequestDTO;
import com.gft.infrastructure.dto.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

/**
 * @package : co.com.carry.microservice.auth.application.user
 * @name : UserService.java
 * @date : 2024-11
 * @author : Isaias Villarreal
 * @version : 1.0.0
 */
public interface UserService {


    Page<UserResponseDTO> findAllByRoleName(String roleName, Pageable pageable);

    UserResponseDTO login(UserLoginDTO userLoginDTO);

    UserResponseDTO loginUsingNIT(LoginUsingNITDTO loginUsingNITDTO);

    /**
     * This method is used to find all clients using pagination
     * @param pageable pageable
     * @return CompletableFuture<Page<ClientDto>>
     */
    CompletableFuture<Page<UserResponseDTO>> findAll(Pageable pageable);

    /**
     * This method is used to find a client by id
     * @param id id
     * @return CompletableFuture<ClientDto>
     */
    CompletableFuture<UserResponseDTO> findById(Long id);

    /**
     * This method is used to find a user by its phone number
     * @param phoneNumber phoneNumber
     * @return CompletableFuture<UserResponseDTO>
     */
    CompletableFuture<UserResponseDTO> findUserByPhoneNumber(String phoneNumber);

    /**
     * This method is used to find a user by its phone number
     * @param email email
     * @return CompletableFuture<UserResponseDTO>
     */
    CompletableFuture<UserResponseDTO> findUserByEmail(String email);

    /**
     * This method is used to find a user by its phone number
     * @param phoneNumber phoneNumber
     * @return CompletableFuture<UserResponseDTO>
     */
    CompletableFuture<HttpStatus> findUserByPhoneNumberIfExists(String phoneNumber);

    /**
     * This method is used to save a new User, receive a generic DTO and a role
     * @param userRequestDTO User Request DTO
     * @param role role
     * @return CompletableFuture<HttpStatus>
     */
    CompletableFuture<HttpStatus> save(UserRequestDTO userRequestDTO, String role, Boolean isEnabled);

    /**
     * This method is used to delete a client
     * @param documentNumber documentNumber
     * @return CompletableFuture<Void>
     */
    CompletableFuture<HttpStatus> delete(Long documentNumber);

    ResponseEntity<?> changePassword(ChangePasswordRequestDTO request, Principal principal);

}
