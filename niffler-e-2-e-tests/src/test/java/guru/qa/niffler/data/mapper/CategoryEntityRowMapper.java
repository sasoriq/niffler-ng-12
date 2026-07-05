package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {

    }

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(rs.getObject("c_id", UUID.class));
        entity.setName(rs.getString("c_name"));
        entity.setUsername(rs.getString("c_username"));
        entity.setArchived(rs.getBoolean("c_archived"));
        return entity;
    }
}
