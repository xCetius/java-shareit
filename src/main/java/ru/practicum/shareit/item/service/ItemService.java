package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemService(BookingRepository bookingRepository,
                       ItemRepository itemRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ItemDto getItem(long id) {
        ItemDto item = itemRepository.findById(id).map(ItemDtoMapper::toItemDto).orElseThrow(() -> new NotFoundException("Item not found with id: " + id));

        item.setComments(commentRepository.findByItemId(id).stream().map(CommentDtoMapper::toCommentDto).toList());
        return item;
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemDtoMapper.toItemDto(item);

                    Optional<Booking> lastBookingOpt = bookingRepository
                            .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                                    item.getId(),
                                    now,
                                    BookingStatus.APPROVED
                            );
                    lastBookingOpt.ifPresent(booking ->
                            itemDto.setLastBooking(BookingDtoMapper.toBookingShortDto(booking)));

                    Optional<Booking> nextBookingOpt = bookingRepository
                            .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                                    item.getId(),
                                    now,
                                    BookingStatus.APPROVED);
                    nextBookingOpt.ifPresent(booking ->
                            itemDto.setNextBooking(BookingDtoMapper.toBookingShortDto(booking)));

                    List<Comment> comments = commentRepository.findByItemId(item.getId());
                    itemDto.setComments(comments.stream()
                            .map(CommentDtoMapper::toCommentDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    public Item addItem(Item item, long userId) {
        if (item.getOwner() == null) {
            User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Owner not found with id: " + userId));
            item.setOwner(owner);
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(long id, ItemUpdateDto item) {
        Item itemToUpdate = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(text).stream().map(ItemDtoMapper::toItemDto).toList();
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {

        if (!bookingRepository.existsByItemIdAndBookerIdAndEndBefore(
                itemId, userId, LocalDateTime.now())) {
            throw new ValidationException("User didn't book this item or booking not finished");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Comment comment = CommentDtoMapper.toEntity(commentDto, item, author);
        Comment savedComment = commentRepository.save(comment);

        return CommentDtoMapper.toCommentDto(savedComment);
    }


}
