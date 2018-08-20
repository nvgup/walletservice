package com.simplecasino.walletservice.exception;

import com.simplecasino.walletservice.dto.BalanceResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {PlayerAlreadyExistException.class})
    protected ResponseEntity<?> handlePlayerAlreadyExistException(RuntimeException ex, WebRequest webRequest) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(),
                HttpStatus.CONFLICT, webRequest);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<?> handleResourceNotFoundException(RuntimeException ex, WebRequest webRequest) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(),
                HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(value = {InsufficientBalanceException.class})
    protected ResponseEntity<?> handleInsufficientBalanceException(InsufficientBalanceException ex, WebRequest webRequest) {
        BalanceResponse response = new BalanceResponse(ex.getMessage(), ex.getBalance());

        return handleExceptionInternal(ex, response, new HttpHeaders(),
                HttpStatus.CONFLICT, webRequest);
    }
}