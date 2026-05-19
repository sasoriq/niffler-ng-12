package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
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

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setDate(2, spend.getSpendDate());
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                spend.setId(generatedKey);
                return spend;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT s.id AS s_id, " +
                            "s.username AS s_username, " +
                            "s.currency AS s_currency, " +
                            "s.spend_date AS s_spend_date, " +
                            "s.amount AS s_amount, " +
                            "s.description AS s_description, " +
                            "c.id AS c_id, c.name AS c_name, " +
                            "c.username AS c_username, c.archived AS c_archived " +
                            "FROM spend s JOIN category c ON s.category_id = c.id WHERE s.id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        SpendEntity entity = extractSpendEntity(rs);
                        return Optional.of(entity);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> spends = new ArrayList<>();
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT s.id AS s_id, " +
                            "s.username AS s_username, " +
                            "s.currency AS s_currency, " +
                            "s.spend_date AS s_spend_date, " +
                            "s.amount AS s_amount, " +
                            "s.description AS s_description, " +
                            "c.id AS c_id, c.name AS c_name, " +
                            "c.username AS c_username, c.archived AS c_archived " +
                            "FROM spend s JOIN category c ON s.category_id = c.id WHERE s.username = ?"
            )) {
                ps.setString(1, username);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        SpendEntity entity = extractSpendEntity(rs);
                        spends.add(entity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return spends;
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, spend.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private SpendEntity extractSpendEntity(ResultSet rs) throws SQLException {
        SpendEntity entity = new SpendEntity();
        entity.setId(rs.getObject("s_id", UUID.class));
        entity.setUsername("s_username");
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