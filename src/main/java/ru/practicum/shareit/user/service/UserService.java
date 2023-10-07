package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUser(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto user);

    void deleteUser(Long userId);

}
