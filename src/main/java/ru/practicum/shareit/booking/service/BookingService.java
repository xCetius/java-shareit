package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.ConfirmationStatus;
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
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new OwnerMismatchException("Access denied");
        }

        return BookingDtoMapper.toBookingDto(booking);
    }

    public List<BookingDto> findUserBookings(Long userId, String state) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, ConfirmationStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, ConfirmationStatus.REJECTED);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> findOwnerBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now);
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, ConfirmationStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, ConfirmationStatus.REJECTED);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
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
        booking.setStatus(ConfirmationStatus.WAITING);

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
            bookingToUpdate.setStatus(ConfirmationStatus.APPROVED);
        } else {
            bookingToUpdate.setStatus(ConfirmationStatus.REJECTED);
        }

        return BookingDtoMapper.toBookingDto(bookingToUpdate);
    }
}
