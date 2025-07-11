package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NonUniqueEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository("InMemoryUserStorageImpl")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emailToIdMap = new HashMap<>();

    @Override
    public User add(User user) {
        if (emailToIdMap.containsKey(user.getEmail())) {
            throw new NonUniqueEmailException("Email already exists: " + user.getEmail());
        }

        user.setId(getNextUserId());
        users.put(user.getId(), user);
        emailToIdMap.put(user.getEmail(), user.getId());
        log.info("User with id {} added", user.getId());
        return user;
    }

    @Override
    public User getById(long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    @Override
    public List<User> getAll() {
        return users.values()
                .stream().toList();
    }

    @Override
    public User updateUser(UserUpdateDto userUpdate, long userId) {
        if (userUpdate.getEmail() != null && emailToIdMap.containsKey(userUpdate.getEmail())) {
            throw new NonUniqueEmailException("Email already exists: " + userUpdate.getEmail());
        }

        User user = Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            emailToIdMap.remove(user.getEmail());
            user.setEmail(userUpdate.getEmail());
            emailToIdMap.put(userUpdate.getEmail(), userId);
        }

        return user;
    }

    @Override
    public void delete(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        emailToIdMap.remove(users.get(userId).getEmail());
        users.remove(userId);

    }

    private long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
