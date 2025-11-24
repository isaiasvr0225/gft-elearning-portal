package com.gft.presentation.controllers.user;

import com.gft.domain.exceptions.UserException;
import com.gft.infrastructure.dto.user.UserErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @package : package co.com.carry.microservice.auth.presentation.controllers.user
 * @name : ClientControllerAdvice.java
 * @date : 2024-08
 * @author : Isaias Villarreal
 * @version : 1.0.0
 */
@RestControllerAdvice
public class UserControllerAdvice {

    /**
     * This method is used to handle ClientException
     * @param userException userException
     * @return ResponseEntity<ClientErrorDto>
     */
    @ExceptionHandler(value = UserException.class)
    public ResponseEntity<UserErrorDTO> handleUserException(UserException userException) {
        return ResponseEntity.status(userException.getHttpStatus()).body(UserErrorDTO.builder()
                .statusCode(userException.getHttpStatus())
                .message(userException.getMessage())
                .build());
    }
}
