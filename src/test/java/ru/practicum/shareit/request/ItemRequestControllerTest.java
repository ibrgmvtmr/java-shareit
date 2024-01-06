package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exceptions.BadPageArgumentException;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @InjectMocks
    ItemRequestController itemRequestController;
    @Mock
    ItemRequestServiceImpl itemRequestService;

    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemRequestController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    public void createRequest() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().requester(1).description("description").build();

        Mockito.when(itemRequestService.create(Mockito.any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests").content(objectMapper.writeValueAsString(itemRequestDto)).header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(itemRequestService, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    public void getRequests() throws Exception {
        List<ItemRequest> itemRequestList = Collections.emptyList();
        Mockito.when(itemRequestService.getAll(Mockito.anyLong())).thenReturn(Collections.emptyList());
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void getRequestById() throws Exception {
        RequestDto itemRequestDto = RequestDto.builder().requester(1).description("description").build();
        Mockito.when(itemRequestService.get(Mockito.anyLong(), Mockito.anyInt())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{id}", 1L).header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(itemRequestService, Mockito.times(1)).get(1L, 1);
    }

    @Test
    public void getRequestsPagination() throws Exception {
        List<ItemRequest> itemRequestList = Collections.emptyList();
        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(get("/requests/all?from=1&size=0").header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(itemRequestService, Mockito.times(1)).getAll(1L, 1, 0);
    }

    @Test
    public void getRequestException() throws Exception {
        Mockito.when(itemRequestService.get(Mockito.anyLong(), Mockito.anyInt())).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/{id}", 1L).header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllRequestsPaginationException() throws Exception {
        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(BadPageArgumentException.class);
        mvc.perform(get("/requests/all?from=1&size=0", 1L).header("X-Sharer-User-Id", 1).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }

}
