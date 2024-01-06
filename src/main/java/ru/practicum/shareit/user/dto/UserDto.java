package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;

}

