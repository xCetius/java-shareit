package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User getById(long userId);

    List<User> getAll();

    User updateUser(UserUpdateDto user, long userId);

    void delete(long userId);
}
