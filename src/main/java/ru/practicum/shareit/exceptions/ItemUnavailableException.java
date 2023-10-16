package ru.practicum.shareit.exceptions;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(String msg) {
        super(msg);
    }
}
