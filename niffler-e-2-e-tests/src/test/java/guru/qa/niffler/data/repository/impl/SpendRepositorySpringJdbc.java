package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(
            con -> {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, spend.getUsername());
                ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());
                return ps;
            },kh);

        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
            "UPDATE \"spend\" SET currency = ?, amount = ?, description = ?, category_id = ? " +
                "WHERE id = ?",
            spend.getCurrency().name(),
            spend.getAmount(),
            spend.getDescription(),
            spend.getCategory().getId(),
            spend.getId()
        );
        return spend;
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id WHERE s.id = ?",
            SpendEntityRowMapper.instance,
            id
        ));
    }

    @Override
    public List<SpendEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id WHERE s.username = ?",
            SpendEntityRowMapper.instance,
            username
        );
    }

    @Override
    public List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id",
            SpendEntityRowMapper.instance
        );
    }

    @Override
    public void delete(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
            "DELETE FROM \"spend\" WHERE id = ?",
            spend.getId()
        );
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c WHERE username = ? AND name = ?",
            CategoryEntityRowMapper.instance,
            username,
            name
        ));
    }

    @Override
    public List<CategoryEntity> findCategoryByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
    public List<CategoryEntity> findAllCategories() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
            "SELECT c.id AS c_id, " +
                "c.name AS c_name, " +
                "c.username AS c_username, " +
                "c.archived AS c_archived FROM \"category\" c",
            CategoryEntityRowMapper.instance
        );
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
            "UPDATE \"category\" SET name = ?, archived = ? WHERE id = ?",
            category.getName(),
            category.isArchived(),
            category.getId()
        );
        return category;
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
            "DELETE FROM \"category\" WHERE id = ?",
            category.getId()
        );
    }
}
