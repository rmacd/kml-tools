package com.rmacd.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class ClientValidationException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<?> handleConstraintViolation(HttpServletRequest request, Throwable e) {
        return new ResponseEntity<>(new CVErrorBody(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    static class CVErrorBody {
        int status;
        String message;
        String timestamp;

        CVErrorBody (HttpStatus status, String message) {
            this.status = status.value();
            this.message = message;
            this.timestamp = DateTimeFormatter.BASIC_ISO_DATE.format(ZonedDateTime.now());
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}
