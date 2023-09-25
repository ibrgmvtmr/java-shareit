package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(NotFoundException notFoundException) {
        return new ErrorResponse(notFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse accessDenideExceptionHandler(AccessDenideException accessDenideException) {
        return new ErrorResponse(accessDenideException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse DataAlreadyExistExceptionHandler(DataAlreadyExistException alreadyEmailExistException) {
        return new ErrorResponse(alreadyEmailExistException.getMessage());
    }
}
