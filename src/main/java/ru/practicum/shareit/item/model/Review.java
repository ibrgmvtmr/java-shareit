package ru.practicum.shareit.item.model;

import java.time.LocalDateTime;

public class Review {
    private String text;
    private String authorName;
    private LocalDateTime created;

    public Review(String text, String authorName, LocalDateTime created) {
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
