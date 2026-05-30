package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

    @Override
    public UserdataUserEntity create(UserdataUserEntity user) {
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
            },kh);
        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT * FROM \"user\" WHERE id = ?",
            UserdataUserEntityRowMapper.instance,
            id
        ));
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT * FROM \"user\" WHERE username = ?",
            UserdataUserEntityRowMapper.instance,
            username
        ));
    }

    @Override
    public void addOutcomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        jdbcTemplate.update(
            "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                "VALUES (?, ?, ?, ?)",
            requester.getId(),
            addressee.getId(),
            FriendshipStatus.PENDING.name(),
            LocalDate.now()
        );
    }

    @Override
    public void addIncomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        addOutcomeInvitation(addressee, requester);
    }

    @Override
    public void addFriend(UserdataUserEntity requester, UserdataUserEntity addressee) {
        jdbcTemplate.update(
            "UPDATE \"friendship\" SET status = ? WHERE requester_id = ? AND addressee_id = ? AND status = ?",
            FriendshipStatus.ACCEPTED.name(),
            requester.getId(),
            addressee.getId(),
            FriendshipStatus.PENDING.name()
        );

        jdbcTemplate.update(
            "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                "VALUES (?, ?, ?, ?)",
            addressee.getId(),
            requester.getId(),
            FriendshipStatus.ACCEPTED.name(),
            LocalDate.now()
        );
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM \"user\"",
            UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserdataUserEntity user) {
        jdbcTemplate.update(
            "DELETE FROM \"user\" WHERE id = ?",
            user.getId()
        );
    }
}
