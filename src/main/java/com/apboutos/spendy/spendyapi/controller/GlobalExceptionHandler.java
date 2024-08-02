package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.exception.*;
import com.apboutos.spendy.spendyapi.response.security.JwtTokenExpiredResponse;
import com.apboutos.spendy.spendyapi.response.user.UserAuthenticationResponse;
import com.apboutos.spendy.spendyapi.response.user.UserConfirmationResponse;
import com.apboutos.spendy.spendyapi.response.user.UserRegistrationResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UsernameTakenException.class)
    ResponseEntity<Object> handleUserNotSavedException(UsernameTakenException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserRegistrationResponse("Username is taken.",null),
                new HttpHeaders(),
                HttpStatus.METHOD_NOT_ALLOWED,
                webRequest
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<Object> handleUserNotFoundException(AuthenticationException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserAuthenticationResponse(exception.getMessage(),null),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                webRequest
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    ResponseEntity<Object> handleExpiredJwtException(AuthenticationException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new JwtTokenExpiredResponse(exception.getMessage()),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                webRequest
        );
    }

    @ExceptionHandler(UserNotSavedException.class)
    ResponseEntity<Object> handleUserNotSavedException(UserNotSavedException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserRegistrationResponse("User not created due to internal error.",null),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                webRequest
        );
    }

    @ExceptionHandler(TokenNotFoundException.class)
    ResponseEntity<Object> handleTokenNotFoundException(TokenNotFoundException exception , WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserConfirmationResponse("Token is invalid."),
                new HttpHeaders(),
                HttpStatus.NOT_ACCEPTABLE,
                webRequest);
    }

    @ExceptionHandler(TokenExpiredException.class)
    ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserConfirmationResponse("Token has expired."),
                new HttpHeaders(),
                HttpStatus.NOT_ACCEPTABLE,
                webRequest);
    }

    @ExceptionHandler(CategoryExistsException.class)
    ResponseEntity<Object> handleCategoryExistsException(CategoryExistsException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserConfirmationResponse("Category already exists."),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                webRequest);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserConfirmationResponse("Category not found."),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                webRequest);
    }

    @ExceptionHandler(CategoryHasEntriesException.class)
    ResponseEntity<Object> handleCategoryHasEntriesException(CategoryHasEntriesException exception, WebRequest webRequest) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                new UserConfirmationResponse(exception.getMessage()),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                webRequest);
    }
}
