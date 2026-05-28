package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) " +
                    "VALUES (?, ?)"
            )
        ) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, pe.encode(user.getPassword()));
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
                authorityPs.clearParameters();
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
            "SELECT * FROM \"user\" u JOIN \"authorities\" a ON u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                AuthUserEntity user = null;
                List<AuthAuthorityEntity> authorities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthAuthorityEntity authority = AuthAuthorityEntityRowMapper.instance.mapRow(rs, 1);
                    authorities.add(authority);
                } if (user != null) {
                    user.setAuthorities(authorities);
                }
                return Optional.ofNullable(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuthUserEntity entity = extractUserEntity(rs);
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        List<AuthUserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * FROM \"user\""
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthUserEntity entity = extractUserEntity(rs);
                    users.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
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

    private AuthUserEntity extractUserEntity(ResultSet rs) throws SQLException {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setId(rs.getObject("id", UUID.class));
        entity.setUsername(rs.getString("username"));
        entity.setPassword(rs.getString("password"));
        entity.setEnabled(rs.getBoolean("enabled"));
        entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return entity;
    }
}
