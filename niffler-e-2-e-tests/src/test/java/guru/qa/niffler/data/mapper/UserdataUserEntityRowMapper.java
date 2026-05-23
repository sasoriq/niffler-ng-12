package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.UserdataUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataUserEntityRowMapper implements RowMapper<UserdataUserEntity> {

    public final static UserdataUserEntityRowMapper instance = new UserdataUserEntityRowMapper();

    private UserdataUserEntityRowMapper() {

    }

    @Override
    public UserdataUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserdataUserEntity entity = new UserdataUserEntity();
        entity.setId(rs.getObject("id", UUID.class));
        entity.setUsername(rs.getString("username"));
        entity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        entity.setFirstname(rs.getString("firstname"));
        entity.setSurname(rs.getString("surname"));
        entity.setFullName(rs.getString("full_name"));
        entity.setPhoto(rs.getBytes("photo"));
        entity.setPhotoSmall(rs.getBytes("photo_small"));
        return entity;
    }
}
