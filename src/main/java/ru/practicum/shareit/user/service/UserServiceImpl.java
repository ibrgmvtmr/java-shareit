package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMappers;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream().map(UserMappers::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException("пользователь с таким id не найден");
        }

        return UserMappers.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMappers.toUser(userDto);
        validation(user);

        User createdUser = userRepository.createUser(user);
        return UserMappers.toUserDto(createdUser);
    }

    private void validation(User user) {
        if (userRepository.getUsers().stream().anyMatch(user1 -> Objects.equals(user1.getEmail(), user.getEmail()))) {
            throw new DataAlreadyExistException("Пользователь с таким email уже существует");
        }

    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMappers.toUser(userDto);
        if (userRepository.getUser(user.getId()) == null) {
            throw new NotFoundException("пользователь с таким id не найден");
        }
        validateEmptyField(user);
        if (userRepository.getUsers().stream().anyMatch(user1 -> Objects.equals(user1.getEmail(), user.getEmail()) && !Objects.equals(user.getId(), user1.getId()))) {
            throw new DataAlreadyExistException("Пользователь с таким email уже существует");
        }
        return UserMappers.toUserDto(userRepository.updateUser(user));
    }

    private void validateEmptyField(User user) {
        User orig = userRepository.getUser(user.getId());

        if (Objects.isNull(user.getName())) {
            user.setName(orig.getName());
        }

        if (Objects.isNull(user.getEmail())) {
            user.setEmail(orig.getEmail());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.getUser(userId) == null) {
            throw new NotFoundException("пользователь с таким id не найден");
        }
        userRepository.deleteUser(userId);
        itemRepository.getItems().stream().filter(e -> Objects.equals(e.getOwner(), userId)).peek(e -> itemRepository.deleteItem(e.getId())).close();
    }
}