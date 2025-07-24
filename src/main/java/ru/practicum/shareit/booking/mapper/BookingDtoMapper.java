package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.mapper.UserDtoMapper;

public class BookingDtoMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemDtoMapper.toItemDto(booking.getItem()),
                UserDtoMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return new BookingShortDto(booking.getId(),
                booking.getStart(),
                booking.getEnd());
    }
}
