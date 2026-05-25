package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private final DataSource dataSource;
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO 'user' (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            },
            kh);
        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM 'user' WHERE id = ?",
                AuthUserEntityRowMapper.instance,
                id
            )
        );
    }

    @Override
    public List<AuthUserEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
            "SELECT * FROM 'users' WHERE username = ?",
            AuthUserEntityRowMapper.instance,
            username
        );
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
            "SELECT * FROM 'users'",
            AuthUserEntityRowMapper.instance
        );
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "DELETE FROM 'user' WHERE id = ?",
            user.getId()
        );
    }
}
