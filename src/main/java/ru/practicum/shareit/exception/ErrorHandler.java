package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(StatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleStatusException(final StatusException e) {
        return Map.of("error", "Incorrect status",
                "description", e.getMessage()

        );

    }

    @ExceptionHandler(ConfirmationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleConfirmationException(final ConfirmationException e) {
        return Map.of("error", "Incorrect confirmation",
                "description", e.getMessage()

        );

    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("error", "Could not find entity.",
                "description", e.getMessage()

        );

    }

    @ExceptionHandler(NonUniqueEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNonUniqueEmailException(final NonUniqueEmailException e) {
        return Map.of("error", "Email is already in use",
                "description", e.getMessage()

        );

    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(cv -> {
            String path = cv.getPropertyPath().toString();
            String message = cv.getMessage();
            errors.put(path, message);
        });
        return errors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntimeException(final RuntimeException e) {
        return Map.of("error", "Runtime error.",
                "description", e.getMessage());

    }


}
