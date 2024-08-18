package net.gb.knox.nudge.handler;

import jakarta.validation.ConstraintViolationException;
import net.gb.knox.nudge.domain.Error;
import net.gb.knox.nudge.exception.EntityMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("handleConstraintViolationException(e: {}): enter", e.getMessage());
        var message = e.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));
        var error = new Error("VALIDATION_FAILED", message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityMissingException.class)
    public ResponseEntity<Error> handleEntityMissingException(EntityMissingException e) {
        logger.warn("handleEntityMissingException(e: {}): enter", e.getMessage());
        var error = new Error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception e) {
        logger.error("handleGenericException(e: {} - {}): enter", e.getClass().getName(), e.getMessage());
        var error = new Error("GENERIC_ERROR", "Something went wrong");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
