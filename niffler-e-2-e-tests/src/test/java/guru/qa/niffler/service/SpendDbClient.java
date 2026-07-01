package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static guru.qa.niffler.data.Databases.dataSource;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
    private final DataSource dataSource = dataSource(CFG.spendJdbcUrl());
    CategoryDaoSpringJdbc categoryDao = new CategoryDaoSpringJdbc(dataSource);
    SpendDaoSpringJdbc spendDao = new SpendDaoSpringJdbc(dataSource);

    private final TransactionTemplate transactionTemplate = new TransactionTemplate(
        new JdbcTransactionManager(dataSource)
    );

    public SpendJson createSpend(SpendJson spend) {
        return transactionTemplate.execute(status -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(spendDao.create(spendEntity));
        });
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transactionTemplate.execute(status -> {
                CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                return CategoryJson.fromEntity(
                    categoryDao.create(categoryEntity)
                );
            }
        );
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return transactionTemplate.execute(status -> {
                CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                return CategoryJson.fromEntity(
                    categoryDao.update(categoryEntity)
                );
            }
        );
    }
}
