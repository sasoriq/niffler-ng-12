package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();


    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            spendPs.setString(1, spend.getUsername());
            spendPs.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            spendPs.setString(3, spend.getCurrency().name());
            spendPs.setDouble(4, spend.getAmount());
            spendPs.setString(5, spend.getDescription());
            spendPs.setObject(6, spend.getCategory().getId());

            spendPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = spendPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "UPDATE \"spend\" SET currency = ?, amount = ?, description = ?, category_id = ? " +
                "WHERE id = ?"
        )) {
            spendPs.setString(1, spend.getCurrency().name());
            spendPs.setDouble(2, spend.getAmount());
            spendPs.setString(3, spend.getDescription());
            spendPs.setObject(4, spend.getCategory().getId());
            spendPs.setObject(5, spend.getId());

            spendPs.executeUpdate();

            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id WHERE s.id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SpendEntity entity = extractSpendEntity(rs);
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findByUsername(String username) {
        List<SpendEntity> spends = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id WHERE s.username = ?"
        )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpendEntity entity = extractSpendEntity(rs);
                    spends.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return spends;
    }

    @Override
    public List<SpendEntity> findAll() {
        List<SpendEntity> spends = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT s.id AS s_id, " +
                "s.username AS s_username, " +
                "s.currency AS s_currency, " +
                "s.spend_date AS s_spend_date, " +
                "s.amount AS s_amount, " +
                "s.description AS s_description, " +
                "c.id AS c_id, c.name AS c_name, " +
                "c.username AS c_username, c.archived AS c_archived " +
                "FROM \"spend\" s JOIN \"category\" c ON s.category_id = c.id"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpendEntity entity = extractSpendEntity(rs);
                    spends.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spends;
    }

    @Override
    public void delete(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"spend\" WHERE id = ?"
        )) {
            ps.setObject(1, spend.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (PreparedStatement catPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "INSERT INTO \"category\" (username, name, archived) " +
                "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            catPs.setString(1, category.getUsername());
            catPs.setString(2, category.getName());
            catPs.setBoolean(3, category.isArchived());

            catPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = catPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT id AS c_id, name AS c_name, username AS c_username, archived AS c_archived " +
                "FROM \"category\" c WHERE id = ?"
        )) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CategoryEntity entity = extractCategoryEntity(rs);
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT id AS c_id, name AS c_name, username AS c_username, archived AS c_archived " +
                "FROM \"category\" c WHERE username = ? AND name = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CategoryEntity entity = extractCategoryEntity(rs);
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findCategoryByUsername(String username) {
        List<CategoryEntity> categories = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT id AS c_id, name AS c_name, username AS c_username, archived AS c_archived " +
                "FROM \"category\" c WHERE username = ?"
        )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CategoryEntity entity = extractCategoryEntity(rs);
                    categories.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        List<CategoryEntity> categories = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "SELECT id AS c_id, name AS c_name, username AS c_username, archived AS c_archived " +
                "FROM \"category\" c"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CategoryEntity entity = extractCategoryEntity(rs);
                    categories.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        try (PreparedStatement catPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "UPDATE \"category\" SET name = ?, archived = ? " +
                "WHERE id = ?"
        )) {
            catPs.setString(1, category.getName());
            catPs.setBoolean(2, category.isArchived());
            catPs.setObject(3, category.getId());

            catPs.executeUpdate();

            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"category\" WHERE id = ?"
        )) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private SpendEntity extractSpendEntity(ResultSet rs) throws SQLException {
        SpendEntity entity = new SpendEntity();
        entity.setId(rs.getObject("s_id", UUID.class));
        entity.setUsername(rs.getString("s_username"));
        entity.setCurrency(CurrencyValues.valueOf(rs.getString("s_currency")));
        entity.setSpendDate(Date.valueOf(rs.getObject("s_spend_date", LocalDate.class)));
        entity.setAmount(rs.getDouble("s_amount"));
        entity.setDescription(rs.getString("s_description"));
        entity.setCategory(extractCategoryEntity(rs));
        return entity;
    }

    private CategoryEntity extractCategoryEntity(ResultSet rs) throws SQLException {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(rs.getObject("c_id", UUID.class));
        entity.setName(rs.getString("c_name"));
        entity.setUsername(rs.getString("c_username"));
        entity.setArchived(rs.getBoolean("c_archived"));
        return entity;
    }
}
