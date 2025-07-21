package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(@Qualifier("InMemoryItemStorageImpl") ItemStorage itemStorage,
                       @Qualifier("InMemoryUserStorageImpl") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto getItem(long id) {
        return itemStorage.getById(id);
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        return itemStorage.getItemsByUserId(userId);
    }

    public Item addItem(Item item, long userId) {
        if (item.getOwner() == null) {
            User owner = userStorage.getById(userId);
            item.setOwner(owner);
        }

        return itemStorage.add(item);
    }

    public Item updateItem(long id, ItemUpdateDto item) {
        return itemStorage.update(id, item);
    }

    public List<Item> search(String text) {
        return itemStorage.search(text);
    }

}
