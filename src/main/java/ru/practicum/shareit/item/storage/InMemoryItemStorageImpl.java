package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("InMemoryItemStorageImpl")
@Slf4j
public class InMemoryItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto getById(long id) {
        return Optional.ofNullable(items.get(id)).map(ItemDtoMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId).map(ItemDtoMapper::toItemDto).toList();
    }

    @Override
    public Item add(Item item) {
        long id = getNextItemId();
        item.setId(id);
        items.put(id, item);
        log.info("Item with id {} added", id);
        return item;
    }

    @Override
    public Item update(long id, ItemUpdateDto itemUpdate) {
        Item item = Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        if (itemUpdate.getName() != null) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream().filter(item -> item.getAvailable().equals(true) && item.getName().toLowerCase().contains(text.toLowerCase())).toList();
    }

    private long getNextItemId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
