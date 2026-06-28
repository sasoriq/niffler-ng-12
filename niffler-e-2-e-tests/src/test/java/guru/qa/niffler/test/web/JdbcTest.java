package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

public class JdbcTest {

    static UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    void spendTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
            new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                    null,
                    "name5",
                    "duck",
                    false
                ),
                CurrencyValues.RUB,
                1000.0,
                "description",
                "duck"
            )
        );
        System.out.println(spend);
    }

    @ValueSource(strings = {
        "person-18"
    })
    @ParameterizedTest
    void userSpringXaTxTest(String username) {
        UserdataUserJson user = usersDbClient.createUser(username);
        System.out.println(user);

        usersDbClient.addIncomeInvitation(user, 1);
        usersDbClient.addOutcomeInvitation(user, 1);
    }
}
