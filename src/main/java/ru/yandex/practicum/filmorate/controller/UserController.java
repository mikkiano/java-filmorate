package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createFilm(@RequestBody @Valid User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        int newId = id++;
        user.setId(newId);
        users.put(newId, user);
        log.debug("Был создан пользователь {} c id {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody @Valid User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Был создан пользователь {} c id {}", user.getName(), user.getId());
            return user;
        } else {
            log.error("Пользователя c id {} не существует", user.getId());
            throw new UserNotExistException();
        }
    }
}
