package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {

    public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

    private AuthAuthorityEntityRowMapper() {

    }

    @Override
    public AuthAuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthAuthorityEntity authority = new AuthAuthorityEntity();
        authority.setId(rs.getObject("id", UUID.class));
        AuthUserEntity user = new AuthUserEntity();
        user.setId(rs.getObject("user_id", UUID.class));
        authority.setUser(user);
        authority.setAuthority(Authority.valueOf(rs.getString("authority")));
        return authority;
    }
}
