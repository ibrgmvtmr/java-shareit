package ru.practicum.shareit.exceptions;

public class BadPageArgumentException extends RuntimeException {
    public BadPageArgumentException(String msg) {
        super(msg);
    }
}
