package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMappers;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void getAllUsersTest() {
        User userOne = User.builder().id(1L).build();
        User userTwo = User.builder().id(2L).build();
        List<User> userList = new ArrayList<>();
        userList.add(userOne);
        userList.add(userTwo);
        when(userRepository.findAll()).thenReturn(userList);
        List<UserDto> userDtos = userService.getUsers();
        assertEquals(2, userDtos.size());
    }

    @Test
    public void getUserest() {
        User userOne = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(userOne));

        UserDto userDto = userService.getUser(1L);
        assertNotNull(userDto);
    }

    @Test
    public void getUserExceptionTest() {
        User userOne = User.builder().id(1L).name("user").email("email@mail.com").build();
        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    public void createUserTest() {
        User userOne = User.builder().id(1L).name("user").email("email@mail.com").build();
        when(userRepository.save(Mockito.any())).thenReturn(userOne);
        assertNotNull(userService.createUser(UserMappers.toUserDto(userOne)));
    }

    @Test
    public void updateTest() {
        User userOne = User.builder().id(1L).build();
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOne));
        when(userRepository.save(Mockito.any())).thenReturn(userOne);
        UserDto userDto = userService.updateUser(UserMappers.toUserDto(userOne));
        assertNotNull(userDto);
        userDto = userService.updateUser(UserMappers.toUserDto(userOne));
        assertNotNull(userDto);
    }

}
