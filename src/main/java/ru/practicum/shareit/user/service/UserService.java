package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        return userRepository.findById(userId).map(UserDtoMapper::toUserDto).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
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
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

}
