package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam(defaultValue = "0", required = false) Integer from,
                                  @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        return itemService.getItem(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto, @PathVariable long id) {
        itemDto.setId(id);
        return itemService.updateItem(itemDto, userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        userService.getUser(userId);
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentAnswerDto getCommentByItemId(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @Valid @RequestBody CommentDto commentDto) {
        commentDto.setItemId(itemId);
        commentDto.setId(userId);
        return itemService.postItemComment(commentDto);
    }
}
