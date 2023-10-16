package ru.practicum.shareit.exceptions;

public class OwnerHasNoRightsException extends RuntimeException {
    public OwnerHasNoRightsException(String msg) {
        super(msg);
    }
}
