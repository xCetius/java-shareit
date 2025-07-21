package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorageImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public UserDto getUser(long userId) {
        return UserDtoMapper.toUserDto(userStorage.getById(userId));
    }

    public List<UserDto> getUsers() {
        return userStorage.getAll().stream().map(UserDtoMapper::toUserDto).toList();
    }

    public User updateUser(UserUpdateDto user, long userId) {
        return userStorage.updateUser(user, userId);
    }

    public void deleteUser(long userId) {
        userStorage.delete(userId);
    }

}
