package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {

    static UsersDbClient usersDbClient = new UsersDbClient();
    static SpendDbClient spendDbClient = new SpendDbClient();

    @Test
    void createSpendTest() {
        SpendJson spend = spendDbClient.createDefaultSpend("duck");
        System.out.println(spend);
    }

    @Test
    void createCategoryTest() {
        CategoryJson category = spendDbClient.createDefaultCategory("duck");
        System.out.println(category);
    }

    @Test
    void updateSpendTest() {
        SpendJson spend = spendDbClient.createDefaultSpend("duck");

        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendEntity.setAmount(20000.13);
        SpendJson updatedSpend = SpendJson.fromEntity(spendEntity);

        System.out.println(updatedSpend);
        spendDbClient.updateSpend(updatedSpend);
    }

    @Test
    void updateCategoryTest() {
        SpendJson spend = spendDbClient.createDefaultSpend("duck");

        CategoryJson category = spend.category();
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        categoryEntity.setName("new_name");

        CategoryJson updatedCategory = CategoryJson.fromEntity(categoryEntity);
        System.out.println(updatedCategory);
        spendDbClient.updateCategory(updatedCategory);
    }

    @Test
    void removeSpendTest() {
        SpendJson spend = spendDbClient.createDefaultSpend("duck");
        System.out.println(spend);
        spendDbClient.removeSpend(spend);
    }

    @Test
    void removeCategoryTest() {
        SpendJson spend = spendDbClient.createDefaultSpend("duck");
        CategoryJson category = spend.category();
        System.out.println(category);
        spendDbClient.removeCategory(category);
    }

    @ValueSource(strings = {
        "person-20"
    })
    @ParameterizedTest
    void userSpringXaTxTest(String username) {
        UserdataUserJson user = usersDbClient.createUser(username);
        System.out.println(user);

        usersDbClient.addIncomeInvitation(user, 1);
        usersDbClient.addOutcomeInvitation(user, 1);
        usersDbClient.addFriend(user, 1);
    }
}
