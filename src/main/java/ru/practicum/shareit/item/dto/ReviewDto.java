package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Validated
public class ReviewDto {

    @NotBlank
    private String text;

    @NotBlank
    private String authorName;

    private LocalDateTime created;

    public ReviewDto(String text, String authorName, LocalDateTime created) {
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
