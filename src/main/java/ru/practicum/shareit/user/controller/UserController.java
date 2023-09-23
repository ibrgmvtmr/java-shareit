package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMappers;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers().stream().map(UserMappers::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return UserMappers.toUserDto(userService.getUser(id));
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto user) {
        return UserMappers.toUserDto(userService.createUser(UserMappers.toUser(user)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto user, @PathVariable Long id) {
        user.setId(id);
        return UserMappers.toUserDto(userService.updateUser(UserMappers.toUser(user)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
