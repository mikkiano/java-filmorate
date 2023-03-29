package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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
        try {
            user = userStorage.updateUser(user);
            log.debug("Был обновлен пользователь {} c id {}", user.getName(), user.getId());
            return user;
        } catch (UserNotExistException e) {
            log.error("Пользователя c id {} не существует", user.getId());
            throw e;
        }
    }

    public User addFriend(int userId, int friendId) {
        try {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);

            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.debug("Пользователю с id {} добавлен друг с id {}", userId, friendId);
            log.debug("Пользователю с id {} добавлен друг с id {}", friendId, userId);

            return user;
        } catch (UserNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public User deleteFriend(int userId, int friendId) {
        try {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);

            if (user.getFriends().remove(friendId)) {
                log.debug("У пользователя с id {} удален друг с id {}", userId, friendId);
            } else {
                log.warn("У пользователя с id {} не было друга с id {}", userId, friendId);
            }

            if (friend.getFriends().remove(userId)) {
                log.debug("У пользователя с id {} удален друг с id {}", friendId, userId);
            } else {
                log.warn("У пользователя с id {} не было друга с id {}", friendId, userId);
            }
            return user;
        } catch (UserNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public List<User> getUserFriends(int userId) {
        try {
            User user = userStorage.getUserById(userId);
            return user.getFriends()
                    .stream()
                    .mapToInt(friendId -> friendId)
                    .mapToObj(this::getUserById)
                    .collect(Collectors.toList());

        } catch (UserNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public List<User> getCommonFriends(int id, int otherId) {
        try {
            User user = userStorage.getUserById(id);
            User otherUser = userStorage.getUserById(otherId);

            Set<Integer> userFriends = user.getFriends();
            Set<Integer> otherUserFriends = otherUser.getFriends();

            return userFriends.stream()
                    .filter(otherUserFriends::contains)
                    .mapToInt(friendId -> friendId)
                    .mapToObj(this::getUserById)
                    .collect(Collectors.toList());

        } catch (UserNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }
}
