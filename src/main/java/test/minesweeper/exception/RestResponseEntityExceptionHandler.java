package test.minesweeper.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import test.minesweeper.dto.ErrorResponse;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class , IllegalStateException.class })
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(ex.getMessage());
        log.warn(ex.getMessage(), ex);
        return handleExceptionInternal(ex, errorResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
