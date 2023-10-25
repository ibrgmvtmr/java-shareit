package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMappers;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMappers::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с таким id не найден"));
        return UserMappers.toUserDto(user);
    }

    @Override
    public UserDto createUser(@Valid UserDto userDto) {
        User user = userRepository.save(UserMappers.toUser(userDto));
        return UserMappers.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMappers.toUser(userDto);
        getUser(user.getId());
        validateEmptyField(user);
        if (userRepository.findAll().stream().anyMatch(user1 -> Objects.equals(user1.getEmail(), user.getEmail()) && !Objects.equals(user.getId(), user1.getId()))) {
            throw new DataAlreadyExistException("Пользователь с таким email уже существует");
        }
        return UserMappers.toUserDto(userRepository.save(user));
    }

    private void validateEmptyField(User user) {
        User orig = UserMappers.toUser(getUser(user.getId()));

        if (Objects.isNull(user.getName())) {
            user.setName(orig.getName());
        }

        if (Objects.isNull(user.getEmail())) {
            user.setEmail(orig.getEmail());
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}