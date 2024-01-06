package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMappers;
import ru.practicum.shareit.item.mapper.ItemMappers;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    public void getAllWithFromSizeNullTest() {
        List<ItemDto> itemDtoList = itemService.getItems(1L, null, 1);
        assertEquals(0, itemDtoList.size());
        itemDtoList = itemService.getItems(1L, 1, null);
        assertEquals(0, itemDtoList.size());
        itemDtoList = itemService.getItems(1L, null, null);
        assertEquals(0, itemDtoList.size());
    }

    @Test
    public void getAllWithFromSizeZeroBadArgumentsPaginationExceptionTest() {
        assertThrows(BadPageArgumentException.class, () -> itemService.getItems(1L, 0, 0));
        assertThrows(BadPageArgumentException.class, () -> itemService.getItems(1L, -1, 0));
        assertThrows(BadPageArgumentException.class, () -> itemService.getItems(1L, -1, 1));
    }

    @Test
    public void getAllSuccessTest() {
        User user = User.builder().id(1L).name("Name").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").build();
        Item itemTwo = Item.builder().name("item2").id(1L).available(true).owner(user).description("description2").build();
        List<Item> itemList = new ArrayList<>();
        Collections.addAll(itemList, itemOne, itemTwo);
        Page<Item> page = new PageImpl(itemList, PageRequest.of(0, 2, Sort.Direction.ASC, "id"), itemList.size());
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Booking.builder().id(1L).item(Item.builder().id(1L).build()).build());
        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Booking.builder().id(1L).item(Item.builder().id(1L).build()).build());
        when(itemRepository.findItemsByOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(page);
        Collections.addAll(itemList, itemOne, itemTwo);
        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);

        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Booking.builder().id(1L).item(Item.builder().id(99L).build()).build());
        when(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Booking.builder().id(1L).item(Item.builder().id(100L).build()).build());
    }

    @Test
    public void updateItemSuccessTest() {
        User user = User.builder().id(1L).name("User").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").requestId(1L).build();
        ItemDto itemDto = ItemMappers.toItemDto(itemOne);
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemOne));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any())).thenReturn(itemOne);
        ItemDto itemDtoOne = itemService.updateItem(itemDto, 1L);
        assertNotNull(itemDtoOne);
    }

    @Test
    public void updateItemNullItemNameDescriptionAvailableTest() {
        User user = User.builder().id(1L).name("User").email("email").build();
        Item itemOne = Item.builder().id(1L).owner(user).requestId(1L).build();
        ItemDto itemDto = ItemMappers.toItemDto(itemOne);
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemOne));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any())).thenReturn(itemOne);
        ItemDto itemDto1 = itemService.updateItem(itemDto, 1L);
        assertNotNull(itemDto1);
    }

    @Test
    public void updateItemNotSuccessTest() {
        User user = User.builder().id(1L).name("User").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").build();
        ItemDto itemDto = ItemMappers.toItemDto(itemOne);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemOne));
        Assertions.assertThrows(AccessDenideException.class, () -> itemService.updateItem(itemDto, 2L));
    }


    @Test
    public void getBookingByIdTest() {
        User user = User.builder().id(1L).name("User").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").build();

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemOne));
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(Collections.emptyList());
        ItemDto itemDto = itemService.getItem(1L, 1L);
        Assertions.assertNotNull(itemDto);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 1L));
    }

    @Test
    public void createBookingTest() {
        User user = User.builder().id(1L).name("User").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").build();
        ItemDto itemDto = ItemMappers.toItemDto(itemOne);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any())).thenReturn(itemOne);
        ItemDto itemDto1 = itemService.createItem(itemDto, 1L);
        assertNotNull(itemDto1);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
    }


    @Test
    public void deleteTest() {
        itemService.deleteItem(1L);
        verify(itemRepository, atMostOnce()).deleteById(Mockito.anyLong());
    }

    @Test
    public void postCommentThrowExceptionOwnerHasNotItem() {
        CommentDto commentDto = CommentDto.builder().itemId(1L).text("Text").id(1L).build();
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(Optional.empty());
        assertThrows(OwnerHasNotItemException.class, () -> itemService.postItemComment(commentDto));
    }

    @Test
    public void postCommentNullBookingAndEndAfterLocalDateNow() {
        Booking booking = Booking.builder().end(LocalDateTime.now().plusHours(2L)).build();
        CommentDto commentDto = CommentDto.builder().itemId(1L).text("Text").id(1L).build();
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(Optional.ofNullable(booking));
        assertThrows(TimeException.class, () -> itemService.postItemComment(commentDto));
    }

    @Test
    public void postCommentSuccess() {
        Booking booking = Booking.builder().end(LocalDateTime.now().minusHours(2L)).build();

        CommentDto commentDto = CommentDto.builder().itemId(1L).text("Text").id(1L).build();

        when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(Optional.ofNullable(booking));

        when(commentRepository.save(any())).thenAnswer(invocation -> {
            Comment commentArgument = invocation.getArgument(0);
            Long idFromDto = commentDto.getId();
            return Comment.builder().id(idFromDto).text(commentArgument.getText()).author(User.builder().id(1L).name("Name").build()).itemId(commentArgument.getItemId()).created(LocalDateTime.now().minusHours(2L)).build();
        });

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));

        CommentAnswerDto commentAnswerDto = itemService.postItemComment(commentDto);

        assertNotNull(commentAnswerDto);
        assertNotNull(commentAnswerDto.getId());
        assertEquals(commentDto.getId(), commentAnswerDto.getId());
    }

    @Test
    public void toComment_shouldMapCommentDtoToComment() {
        CommentDto commentDto = CommentDto.builder().id(1L).text("test").itemId(2L).build();

        Comment comment = CommentMappers.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getAuthor().getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getItemId(), comment.getItemId());
    }

    @Test
    public void toAnswerDto_shouldMapCommentToCommentAnswerDto() {
        Comment comment = Comment.builder().id(1L).text("test").author(User.builder().id(2L).name("Name").build()).itemId(3L).created(LocalDateTime.now()).build();

        CommentAnswerDto commentAnswerDto = CommentMappers.toAnswerDto(comment);

        assertNotNull(commentAnswerDto);
        assertEquals(comment.getId(), commentAnswerDto.getId());
        assertEquals(comment.getCreated(), commentAnswerDto.getCreated());
        assertEquals(comment.getAuthor().getName(), commentAnswerDto.getAuthorName());
        assertEquals(comment.getText(), commentAnswerDto.getText());
    }

    @Test
    public void searchTextSucces() {
        User user = User.builder().id(1L).name("Name").email("email").build();
        Item itemOne = Item.builder().name("item1").id(1L).available(true).owner(user).description("description1").build();
        Item itemTwo = Item.builder().name("item2").id(1L).available(true).owner(user).description("description2").build();
        List<Item> itemList = new ArrayList<>();
        Collections.addAll(itemList, itemOne, itemTwo);
        Page<Item> page = new PageImpl(itemList, PageRequest.of(0, 2, Sort.Direction.ASC, "id"), itemList.size());

        when(itemRepository.searchByText(any(), any())).thenReturn(page);
        List<ItemDto> itemDtoList = itemService.search("Mock", 1, 1);
        assertEquals(2, itemDtoList.size());
        itemDtoList = itemService.search("Mock", 0, 1);
        assertEquals(2, itemDtoList.size());
    }


    @Test
    void searchEmptyTextShouldReturnEmptyList() {
        List<ItemDto> itemDtoList = itemService.search("", 0, 1);

        assertEquals(0, itemDtoList.size());
    }

    @Test
    void searchInvalidPageParametersShouldThrowException() {
        try {
            itemService.search("Mock", -1, 1);
        } catch (BadPageArgumentException e) {
            assertEquals("такой страницы не существует", e.getMessage());
        }

        try {
            itemService.search("Mock", 0, 0);
        } catch (BadPageArgumentException e) {
            assertEquals("такой страницы не существует", e.getMessage());
        }
    }

}
