package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"user\" (username, spend_date, currency, amount, description, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) " +
                    "VALUES (?, ?)"
            )
        ) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            for (AuthAuthorityEntity authority : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, authority.getAuthority().name());
                authorityPs.addBatch();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT " +
                "u.id AS u_id, " +
                "u.username AS username, " +
                "u.password AS password, " +
                "u.enabled AS enabled, " +
                "u.account_non_expired AS account_non_expired, " +
                "u.account_non_locked AS account_non_locked, " +
                "u.credentials_non_expired AS credentials_non_expired, " +
                "a.id AS a_id, " +
                "a.user_id AS a_user_id, " +
                "a.authority AS authority " +
                "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return Optional.ofNullable(extractData(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT " +
                "u.id AS u_id, " +
                "u.username AS username, " +
                "u.password AS password, " +
                "u.enabled AS enabled, " +
                "u.account_non_expired AS account_non_expired, " +
                "u.account_non_locked AS account_non_locked, " +
                "u.credentials_non_expired AS credentials_non_expired, " +
                "a.id AS a_id, " +
                "a.user_id AS a_user_id, " +
                "a.authority AS authority " +
                "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id WHERE u.username = ?"
        )) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return Optional.ofNullable(extractData(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT " +
                "u.id AS u_id, " +
                "u.username AS username, " +
                "u.password AS password, " +
                "u.enabled AS enabled, " +
                "u.account_non_expired AS account_non_expired, " +
                "u.account_non_locked AS account_non_locked, " +
                "u.credentials_non_expired AS credentials_non_expired, " +
                "a.id AS a_id, " +
                "a.user_id AS a_user_id, " +
                "a.authority AS authority " +
                "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                return extractAllData(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthUserEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthUserEntity extractData(ResultSet rs) throws SQLException {
        Map<UUID, AuthUserEntity> userMap = new HashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("u_id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    return extractUserEntity(rs);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            AuthAuthorityEntity authority = extractAuthorityEntity(rs, user);
            user.getAuthorities().add(authority);
        }
        return userMap.get(userId) ;
    }

    private List<AuthUserEntity> extractAllData(ResultSet rs) throws SQLException {
        Map<UUID, AuthUserEntity> userMap = new HashMap<>();
        while (rs.next()) {
            UUID userId = rs.getObject("u_id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    return extractUserEntity(rs);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            AuthAuthorityEntity authority = extractAuthorityEntity(rs, user);
            user.getAuthorities().add(authority);
        }
        return new ArrayList<>(userMap.values());
    }

    private AuthUserEntity extractUserEntity(ResultSet rs) throws SQLException {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setId(rs.getObject("u_id", UUID.class));
        entity.setUsername(rs.getString("username"));
        entity.setPassword(rs.getString("password"));
        entity.setEnabled(rs.getBoolean("enabled"));
        entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return entity;
    }

    private AuthAuthorityEntity extractAuthorityEntity(ResultSet rs, AuthUserEntity user) throws SQLException {
        AuthAuthorityEntity authority = new AuthAuthorityEntity();
        authority.setId(rs.getObject("a_id", UUID.class));
        authority.setUser(user);
        authority.setAuthority(Authority.valueOf(rs.getString("authority")));
        return authority;
    }
}
