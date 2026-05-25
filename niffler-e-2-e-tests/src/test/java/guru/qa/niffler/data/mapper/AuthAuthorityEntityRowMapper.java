package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.Authority;
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
        AuthAuthorityEntity entity = new AuthAuthorityEntity();
        entity.setId(rs.getObject("id", UUID.class));
        entity.setUser(rs.getObject("user_id", AuthUserEntity.class));
        entity.setAuthority(Authority.valueOf(rs.getString("authority")));
        return entity;
    }
}
