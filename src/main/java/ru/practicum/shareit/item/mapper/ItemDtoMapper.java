package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemDtoMapper {

    public static ItemDto toItemDto(Item item) {
        List<CommentDto> commentDtos = item.getComments() != null ?
                item.getComments().stream()
                        .map(CommentDtoMapper::toCommentDto)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                commentDtos

        );
    }

//    public static ItemForUserDto toForUserDto(Item item) {
//        return new ItemForUserDto(
//                item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                item.getRequest() != null ? item.getRequest().getId() : null,
//                null,  // lastBooking
//                null,  // nextBooking
//                Collections.emptyList()  // comments
//        );
//    }


}
