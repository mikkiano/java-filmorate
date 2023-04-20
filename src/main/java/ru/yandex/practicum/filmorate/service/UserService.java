package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        user = userStorage.createUser(user);
        log.debug("Был создан пользователь {} c id {}", user.getName(), user.getId());
        return user;
    }

    public User updateUser(User user) {
        userStorage.getUserById(user.getId()).orElseThrow(UserNotExistException::new);
        user = userStorage.updateUser(user);
        log.debug("Был обновлен пользователь {} c id {}", user.getName(), user.getId());
        return user;
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(UserNotExistException::new);
        User friend = userStorage.getUserById(friendId).orElseThrow(UserNotExistException::new);

        userStorage.addFriend(userId, friendId);
        log.debug("Пользователю с id {} добавлен друг с id {}", userId, friendId);

        return user;
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.getUserById(userId).orElseThrow(UserNotExistException::new);
        userStorage.getUserById(friendId).orElseThrow(UserNotExistException::new);

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        userStorage.getUserById(userId).orElseThrow(UserNotExistException::new);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        userStorage.getUserById(id).orElseThrow(UserNotExistException::new);
        userStorage.getUserById(otherId).orElseThrow(UserNotExistException::new);

        List<User> userFriends = userStorage.getUserFriends(id);
        List<User> otherUserFriends = userStorage.getUserFriends(otherId);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id).orElseThrow(UserNotExistException::new);
    }
}
