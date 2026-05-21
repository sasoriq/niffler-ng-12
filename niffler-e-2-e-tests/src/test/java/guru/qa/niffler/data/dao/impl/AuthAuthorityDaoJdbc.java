package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<AuthAuthorityEntity> create(List<AuthAuthorityEntity> authorities) {
        try (PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO 'authority' (user_id, authority) " +
                "VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthAuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getUser());
                ps.setObject(2, authority.getAuthority());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        authority.setId(rs.getObject("id", UUID.class));
                    }
                }
            }
            return authorities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
