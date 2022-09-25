package gtcloud.plugin.repository.dao;

import gtcloud.plugin.repository.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public User findByUsername(String username) {
        String sql = "select user_id, username, password_hash, salt, role from user where username = :username";
        SqlParameterSource sqlParameters = new MapSqlParameterSource().addValue("username", username);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, this::mapToUser);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private User mapToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
