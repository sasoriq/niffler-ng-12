package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomSentence;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();
    private final SpendRepository spendRep = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
        CFG.spendJdbcUrl()
    );

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            spendEntity.setCategory(resolveCategory(spendEntity));
            return SpendJson.fromEntity(
                spendRep.create(spendEntity)
            );
        });
    }

    public SpendJson createDefaultSpend(String username) {
        return createSpend(
            SpendJson.fromEntity(
                createSpendEntity(username)
            ));
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {

            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(
                spendRep.createCategory(categoryEntity)
            );
        });
    }

    public CategoryJson createDefaultCategory(String username) {
        return createCategory(
            CategoryJson.fromEntity(
                createCategoryEntity(username)
            ));
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            return SpendJson.fromEntity(
                spendRep.update(spendEntity)
            );
        });
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(
                spendRep.updateCategory(categoryEntity)
            );
        });
    }

    @Override
    public void removeSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            spendRep.delete(spendEntity);
            return null;
        });
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            System.out.println("JSON id = " + category.id());
            System.out.println("Entity id = " + categoryEntity.getId());
            spendRep.deleteCategory(categoryEntity);
            return null;
        });
    }

    private CategoryEntity resolveCategory(SpendEntity spend) {
        CategoryEntity category = spend.getCategory();
        if (category.getId() != null) {
            return category;
        }
        return spendRep
            .findCategoryByUsernameAndCategoryName(spend.getUsername(), spend.getCategory().getName())
            .orElseGet(() -> spendRep.createCategory(category));
    }

    private SpendEntity createSpendEntity(String username) {
        SpendEntity spend = new SpendEntity();
        spend.setSpendDate(new Date());
        spend.setCurrency(CurrencyValues.RUB);
        spend.setAmount(1000.00);
        spend.setDescription(randomSentence(2));
        spend.setUsername(username);

        spend.setCategory(createCategoryEntity(username));
        return spend;
    }

    private CategoryEntity createCategoryEntity(String username) {
        CategoryEntity category = new CategoryEntity();
        category.setName(randomCategoryName());
        category.setUsername(username);
        category.setArchived(false);

        return category;
    }


}
