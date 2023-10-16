package ru.practicum.shareit.exceptions;

public class OwnerHasNotItemException extends RuntimeException {
    public OwnerHasNotItemException(String msg) {
        super(msg);
    }
}
