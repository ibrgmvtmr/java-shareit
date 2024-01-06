package ru.practicum.shareit.Item;

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
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    ItemServiceImpl itemService;

    @Mock
    UserService userService;

    @InjectMocks
    ItemController itemController;
    ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    public void saveItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();

        Mockito.when(itemService.createItem(Mockito.any(), Mockito.anyLong())).thenReturn(itemDto);
        mvc.perform(post("/items").content(mapper.writeValueAsString(itemDto)).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getAllItems() throws Exception {
        List<ItemDto> itemList = new ArrayList<>();
        Mockito.when(itemService.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemList);
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

    }

    @Test
    public void getAllBadPagination() throws Exception {
        Mockito.when(itemService.getItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(BadPageArgumentException.class);
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void getItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();
        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/{id}", itemDto.getId()).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

    }

    public void getItemNotFoundUser() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();

        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{id}", itemDto.getId()).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void getNotFoundItem() throws Exception {
        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong())).thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{id}", 1l).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void deleteItemById() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();
        mvc.perform(delete("/items/{id}", itemDto.getId()).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void updateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();
        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", itemDto.getId()).content(mapper.writeValueAsString(itemDto)).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void updateItemException() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();
        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.anyLong())).thenThrow(AccessDenideException.class);
        mvc.perform(patch("/items/{id}", itemDto.getId()).content(mapper.writeValueAsString(itemDto)).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    public void searchItem() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).available(true).name("item").description("description").ownerId(1L).build();
        Mockito.when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(get("/items/search?text='text'").header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("text").itemId(1).id(1).build();
        CommentAnswerDto commentAnswerDto = CommentAnswerDto.builder().id(1).text("text").authorName("name").build();
        Mockito.when(itemService.postItemComment(Mockito.any())).thenReturn(commentAnswerDto);
        mvc.perform(post("/items/{id}/comment", 1L).content(mapper.writeValueAsString(commentDto)).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void createCommentOwnerHasNotItem() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("text").itemId(1).id(1).build();
        CommentAnswerDto commentAnswerDto = CommentAnswerDto.builder().id(1).text("text").authorName("name").build();
        Mockito.when(itemService.postItemComment(Mockito.any())).thenThrow(OwnerHasNotItemException.class);
        mvc.perform(post("/items/{id}/comment", 1L).content(mapper.writeValueAsString(commentDto)).header("X-Sharer-User-Id", 1L).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }
}
