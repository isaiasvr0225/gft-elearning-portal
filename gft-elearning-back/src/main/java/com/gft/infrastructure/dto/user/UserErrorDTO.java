package com.gft.infrastructure.dto.user;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * This is a DTO class for Client error handling
 * @package : com.sprayl.infrastructure.persistence.dto
 * @name : ClientErrorDto.java
 * @date : 2024-08
 * @author : Isaias Villarreal
 * @version : 1.0.0
 */
@Builder
public record UserErrorDTO(
        HttpStatus statusCode,
        String message
) implements Serializable {}
