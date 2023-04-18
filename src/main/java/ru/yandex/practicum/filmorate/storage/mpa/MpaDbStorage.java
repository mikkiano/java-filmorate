package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT ID, NAME FROM MPA";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Mpa.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build());
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT ID, NAME FROM MPA WHERE ID = ?", id);

        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("ID"),
                    mpaRows.getString("NAME"));
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }
}
