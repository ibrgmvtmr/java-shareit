package ru.practicum.shareit.exceptions;

public class NullException extends RuntimeException {
    private String msg;

    public NullException(String msg) {
        super(msg);
    }
}
