package songservice.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import error.BasicExceptionResponse;
import songservice.exception.SongAlreadyExists;
import songservice.exception.SongMetadataNotFound;
import error.ValidationExceptionResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SongExceptionHandler {

    @ExceptionHandler(SongMetadataNotFound.class)
    public ResponseEntity<BasicExceptionResponse> handleResourceNotFound(SongMetadataNotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new BasicExceptionResponse(ex.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionResponse> handleMethodArgumentException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(400)
                .body(new ValidationExceptionResponse("Validation failed", 400, errors));
    }

    @ExceptionHandler(SongAlreadyExists.class)
    public ResponseEntity<BasicExceptionResponse> handleResourceConflict(SongAlreadyExists ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new BasicExceptionResponse(ex.getMessage(), 409));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(cv -> {
            errors.put(cv.getPropertyPath().toString(), cv.getMessage());
        });
        return ResponseEntity
                .status(400)
                .body(new ValidationExceptionResponse(e.getMessage(), 400, errors));
    }
}
