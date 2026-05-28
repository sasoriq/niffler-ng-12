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
        UserdataUserJson user = usersDbClient.createUserSpringJdbcXaTx(
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
    void userSpringTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUserSpringJdbcTx(
            new UserdataUserJson(
                null,
                "person-2",
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
    void userXaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUserJdbcXaTx(
            new UserdataUserJson(
                null,
                "person-3",
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
    void userTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUserJdbcTx(
            new UserdataUserJson(
                null,
                "person-4",
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
