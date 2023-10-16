package ru.practicum.shareit.exceptions;

public class IncorrectStateException extends RuntimeException {
    public IncorrectStateException(String msg) {
        super(msg);
    }
}
