package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaStorage mpaStorage;

    @Test
    void findAll() {
        List<Mpa> mpa = mpaStorage.findAll();
        assertNotNull(mpa);
    }

    @Test
    void getMpaById() {
        Mpa mpa = mpaStorage.getMpaById(1).get();
        assertNotNull(mpa);
    }
}