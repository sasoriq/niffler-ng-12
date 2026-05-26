package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(List<AuthAuthorityEntity> authorities) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
            "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, authorities.get(i).getUser().getId());
                    ps.setString(2, authorities.get(i).getAuthority().name());
                }

                @Override
                public int getBatchSize() {
                    return authorities.size();
                }
            }
        );

    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
            "SELECT * FROM \"authority\"",
            AuthAuthorityEntityRowMapper.instance
        );
    }
}
