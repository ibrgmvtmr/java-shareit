package ru.practicum.shareit.exceptions;

public class DataAlreadyExistException extends RuntimeException {
    public DataAlreadyExistException(String msg) {
        super(msg);
    }
}
