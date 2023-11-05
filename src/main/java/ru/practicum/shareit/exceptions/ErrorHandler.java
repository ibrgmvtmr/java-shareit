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
    public ErrorResponse dataAlreadyExistExceptionHandler(DataAlreadyExistException alreadyEmailExistException) {
        return new ErrorResponse(alreadyEmailExistException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse incorrectStateException(IncorrectStateException incorrectStateException) {
        return new ErrorResponse(incorrectStateException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse bookingTimeException(BookingTimeException bookingTimeException) {
        return new ErrorResponse(bookingTimeException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ownerHasNoItemException(OwnerHasNotItemException ownerHasNotItemException) {
        return new ErrorResponse(ownerHasNotItemException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse timeException(TimeException timeException) {
        return new ErrorResponse(timeException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemUnavailableException(ItemUnavailableException itemUnavailableException) {
        return new ErrorResponse(itemUnavailableException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unsupportedStateException(UnsupportedStateException unsupportedStateException) {
        return new ErrorResponse(unsupportedStateException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ownerHasNoRightsException(OwnerHasNoRightsException ownerHasNoRightsException) {
        return new ErrorResponse(ownerHasNoRightsException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badPageArgumentException(BadPageArgumentException badPageArgumentException) {
        return new ErrorResponse(badPageArgumentException.getMessage());
    }
}
