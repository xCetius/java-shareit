package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingState;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerMismatchException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public BookingDto findBookingById(Long bookingId, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new OwnerMismatchException("Access denied");
        }

        return BookingDtoMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> findUserBookings(Long userId, String state) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }

        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerId(userId, newestFirst);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                    userId, now, now, newestFirst);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(
                    userId, now, newestFirst);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(
                    userId, now, newestFirst);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(
                    userId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(
                    userId, BookingStatus.REJECTED, newestFirst);
        };

        return bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> findOwnerBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }

        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, newestFirst);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                    userId, now, now, newestFirst);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBefore(userId, now, newestFirst);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfter(userId, now, newestFirst);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(
                    userId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(
                    userId, BookingStatus.REJECTED, newestFirst);
        };

        return bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto addBooking(BookingRequestDto bookingRequestDto, long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingRequestDto.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is unavailable");
        }

        if (item.getOwner().getId() == userId) {
            throw new ValidationException("Owner cannot book his own item");
        }


        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        return BookingDtoMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto updateBookingStatus(long bookingId, long userId, boolean approved) {
        long ownerId = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId))
                .getItem().getOwner().getId();
        if (ownerId != userId) {
            throw new OwnerMismatchException("Only owner can update booking of the item");
        }

        Booking bookingToUpdate = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
        if (approved) {
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else {
            bookingToUpdate.setStatus(BookingStatus.REJECTED);
        }

        return BookingDtoMapper.toBookingDto(bookingToUpdate);
    }
}
