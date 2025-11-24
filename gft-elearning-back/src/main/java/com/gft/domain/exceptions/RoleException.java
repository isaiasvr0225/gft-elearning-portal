package com.gft.domain.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RoleException extends RuntimeException {

    private HttpStatus httpStatus;

    public RoleException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
