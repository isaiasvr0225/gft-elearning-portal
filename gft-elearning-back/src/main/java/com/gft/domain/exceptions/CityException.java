package com.gft.domain.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CityException extends RuntimeException {

    private HttpStatus httpStatus;

    public CityException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
