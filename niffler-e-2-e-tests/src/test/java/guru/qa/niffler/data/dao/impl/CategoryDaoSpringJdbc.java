package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO \"category\" (username, name, archived) " +
                    "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c WHERE id = ?",
            CategoryEntityRowMapper.instance,
            id
        ));
    }

    @Override
    public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c WHERE username = ? AND name = ?",
            CategoryEntityRowMapper.instance,
            username,
            categoryName
        ));
    }

    @Override
    public List<CategoryEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c WHERE username = ?",
            CategoryEntityRowMapper.instance,
            username
        );
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c",
            CategoryEntityRowMapper.instance
        );
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "UPDATE \"category\" SET name = ?, archived = ? WHERE id = ?",
            category.getName(),
            category.isArchived(),
            category.getId()
        );
        return category;
    }

    @Override
    public void delete(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
            "DELETE FROM \"category\" WHERE id = ?",
            category.getId()
        );
    }
}
