package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper instance = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {

    }

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
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
