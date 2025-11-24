package com.gft.domain.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class DocumentTypeException extends RuntimeException {

    private HttpStatus httpStatus;

    public DocumentTypeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
