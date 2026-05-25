package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private final Connection connection;

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO 'user' (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, pe.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM 'user' WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
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
    public List<AuthUserEntity> findAllByUsername(String username) {
        List<AuthUserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM 'user' WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
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
    public List<AuthUserEntity> findAll() {
        List<AuthUserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM 'user'"
        )) {
            try (ResultSet rs = ps.getResultSet()) {
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
    public void deleteUser(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM 'user' WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthUserEntity extractUserEntity(ResultSet rs) throws SQLException {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setUsername(rs.getString("username"));
        entity.setPassword(rs.getString("password"));
        entity.setEnabled(rs.getBoolean("enabled"));
        entity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        entity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        entity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return entity;
    }
}
