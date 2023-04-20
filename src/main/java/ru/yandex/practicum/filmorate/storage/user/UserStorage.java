package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(int id);

    void addFriend(int userId, int friendId);

    List<User> getUserFriends(int userId);

    void deleteFriend(int userId, int friendId);

}
