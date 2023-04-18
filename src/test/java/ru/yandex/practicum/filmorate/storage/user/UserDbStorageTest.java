package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        userStorage.createUser(
                User.builder()
                        .email("test@mail.com")
                        .login("test")
                        .name("test")
                        .birthday(LocalDate.of(2002, 12, 1))
                        .build());
        userStorage.createUser(
                User.builder()
                        .email("test@ya.ru")
                        .login("test2")
                        .name("test2")
                        .birthday(LocalDate.of(2000, 8, 19))
                        .build());
    }

    @Test
    void findAll() {
        List<User> users = userStorage.findAll();
        assertNotNull(users);
    }

    @Test
    @DirtiesContext
    void createUser() {
        User user = User.builder()
                .email("test@ya.ru")
                .login("test2")
                .name("test2")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userStorage.createUser(user);
        assertNotNull(user.getId());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        User user = userStorage.getUserById(1).get();
        user.setName("Changed");
        userStorage.updateUser(user);

        assertEquals(userStorage.getUserById(1).get().getName(), "Changed");
    }

    @Test
    void getUserById() {
        User user = userStorage.getUserById(1).get();
        assertNotNull(user);
    }

    @Test
    @DirtiesContext
    void addFriend() {
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getUserFriends(1);
        assertNotNull(friends);
        assertEquals(1, friends.size());
    }

    @Test
    @DirtiesContext
    void getUserFriends() {
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getUserFriends(1);
        assertNotNull(friends);
        assertEquals(1, friends.size());
    }

    @Test
    @DirtiesContext
    void deleteFriend() {
        userStorage.addFriend(1, 2);
        userStorage.deleteFriend(1, 2);
        List<User> friends = userStorage.getUserFriends(1);
        assertNotNull(friends);
        assertEquals(0, friends.size());
    }
}