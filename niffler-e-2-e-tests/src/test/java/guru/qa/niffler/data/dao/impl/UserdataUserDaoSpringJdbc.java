package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserdataUserEntity create(UserdataUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE id = ?",
                UserdataUserEntityRowMapper.instance,
                id
            )
        );
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT * FROM \"user\" WHERE username = ?",
            UserdataUserEntityRowMapper.instance,
            username
        ));
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
            "SELECT * FROM \"user\"",
            UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserdataUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
            "DELETE FROM \"user\" WHERE id = ?",
            user.getId()
        );
    }
}
