package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserdataUserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {

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

    @Test
    void userSpringXaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUser(
            new UserdataUserJson(
                null,
                "person-5",
                CurrencyValues.RUB,
                null,
                null,
                null,
                null,
                null
            )
        );
        System.out.println(user);
    }

    @Test
    void userJdbcXaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUserJdbc(
            new UserdataUserJson(
                null,
                "person-12",
                CurrencyValues.RUB,
                null,
                null,
                null,
                null,
                null
            )
        );
        System.out.println(user);
    }
}
