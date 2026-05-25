package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.Authority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(List<AuthAuthorityEntity> authorities) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        List<AuthAuthorityEntity> authorities = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
            "SELECT * from 'authority'"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthAuthorityEntity entity = new AuthAuthorityEntity();
                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUser(rs.getObject("user_id", AuthUserEntity.class));
                    entity.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorities.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }
}
