package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        return users;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, new String[]{"ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE ID = ?", id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("ID"))
                    .email(userRows.getString("EMAIL"))
                    .login(userRows.getString("LOGIN"))
                    .name(userRows.getString("NAME"))
                    .birthday(Objects.requireNonNull(userRows.getDate("BIRTHDAY")).toLocalDate())
                    .build();
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String acceptance = "SELECT * FROM USER_FRIEND WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet userFriendsRows = jdbcTemplate.queryForRowSet(acceptance, friendId, userId);
        if (userFriendsRows.next()) {
            String friendAccept = "INSERT INTO USER_FRIEND (USER_ID, FRIEND_ID, IS_ACCEPTED) VALUES (?, ?, 'true')";
            jdbcTemplate.update(friendAccept, userId, friendId);
            String userAccept = "UPDATE USER_FRIEND SET IS_ACCEPTED = TRUE WHERE USER_ID = ? AND FRIEND_ID = ?";
            jdbcTemplate.update(userAccept, friendId, userId);
        } else {
            String notAccepted = "INSERT INTO USER_FRIEND (USER_ID, FRIEND_ID) VALUES (?, ?)";
            jdbcTemplate.update(notAccepted, userId, friendId);
        }
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String friends = "SELECT U.* FROM USER_FRIEND UF JOIN USERS U on UF.FRIEND_ID = U.ID  WHERE uf.USER_ID = ?";
        return jdbcTemplate.query(friends, this::mapRowToUser, userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM USER_FRIEND  WHERE USER_ID  = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
        jdbcTemplate.update(sql, friendId, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int i) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
