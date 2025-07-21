package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    ItemDto getById(long id);

    List<ItemDto> getItemsByUserId(long userId);

    Item add(Item item);

    Item update(long id, ItemUpdateDto item);

    List<Item> search(String text);
}
