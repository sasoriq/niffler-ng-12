package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.UserdataUserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private final DataSource dataSource;

    public UserdataUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserdataUserEntity create(UserdataUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photoSmall) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setString(3, user.getFirstname());
                ps.setString(4, user.getSurname());
                ps.setString(5, user.getFullName());
                ps.setBytes(6, user.getPhoto());
                ps.setBytes(7, user.getPhotoSmall());
                return ps;
            },
            kh);
        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE id = ?",
                UserdataUserEntityRowMapper.instance,
                id
            )
        );
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT * FROM \"user\" WHERE username = ?",
            UserdataUserEntityRowMapper.instance,
            username
        ));
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
            "SELECT * FROM \"user\"",
            UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserdataUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "DELETE FROM \"user\" WHERE id = ?",
            user.getId()
        );
    }
}
