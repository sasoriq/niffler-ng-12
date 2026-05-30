package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(List<AuthAuthorityEntity> authorities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"authority\" (user_id, authority) " +
                "VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthAuthorityEntity authority : authorities) {
                ps.setObject(1, authority.getUser().getId());
                ps.setString(2, authority.getAuthority().name());
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
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "SELECT * from \"authority\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthAuthorityEntity authority = new AuthAuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    AuthUserEntity user = new AuthUserEntity();
                    user.setId(rs.getObject("user_id", UUID.class));
                    authority.setUser(user);
                    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorities.add(authority);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }
}
