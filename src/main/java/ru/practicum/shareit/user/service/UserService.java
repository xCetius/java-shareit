package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public UserDto getUser(long userId) {
        return userRepository.findById(userId).map(UserDtoMapper::toUserDto).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserDtoMapper::toUserDto).toList();
    }

    @Transactional
    public User updateUser(UserUpdateDto user, long userId) {
        User userToUpdate = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

}
