package com.solarl.education.controller;

import com.solarl.education.response.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationResponse> onValidationException(ValidationException exception) {
        System.out.println("Обрабатываем ошибку ValidationException");
        return new ResponseEntity<>(
                new ValidationResponse(
                        List.of(String.format("Ошибка validation by contract: %s", exception.getMessage()))),
                HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationResponse> onConstraintViolationException(
            ConstraintViolationException exception) {
        System.out.println("Обрабатываем ошибку ConstraintViolationException");
        List<String> errors = exception
                .getConstraintViolations()
                .stream()
                .map(error -> String.format("Ошибка bean validation: %s",
                                            error.getMessage()))
                .toList();
        return new ResponseEntity<>(new ValidationResponse(errors), HttpStatus.BAD_REQUEST);
    }

}