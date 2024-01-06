package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.DataAlreadyExistException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    public void saveUserWithEmptyException() throws Exception {
        UserDto userDto = UserDto.builder().name("Name").build();

        mvc.perform(post("/users").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
        Mockito.verify(userService, Mockito.atMostOnce()).createUser(Mockito.any());
    }

    @Test
    public void saveUserWithErrorFormatException() throws Exception {
        UserDto userDto = UserDto.builder().name("Name").email("email").build();

        mvc.perform(post("/users").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());

    }

    @Test
    public void saveUserWithoutName() throws Exception {
        UserDto userDto = UserDto.builder().email("email").build();

        mvc.perform(post("/users").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void saveUser() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.createUser(Mockito.any())).thenReturn(userDto);

        mvc.perform(post("/users").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getUser() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.getUser(Mockito.anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/1").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(userService, Mockito.atMostOnce()).getUser(Mockito.anyLong());
    }

    @Test
    public void getUserNotFound() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.getUser(Mockito.anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/{id}", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    public void updateUserEmailExistException() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.updateUser(Mockito.any())).thenThrow(DataAlreadyExistException.class);

        mvc.perform(patch("/users/{id}", 1L).content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL_VALUE)).andExpect(status().isConflict());
    }

    @Test
    public void getAllUser() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.getUsers()).thenReturn(new ArrayList<>());

        mvc.perform(get("/users").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        when(userService.updateUser(Mockito.any())).thenReturn(userDto);

        mvc.perform(patch("/users/1").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void deleteUser() throws Exception {
        UserDto userDto = UserDto.builder().name("user").email("email@email.com").build();
        mvc.perform(delete("/users/1").content(mapper.writeValueAsString(userDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
