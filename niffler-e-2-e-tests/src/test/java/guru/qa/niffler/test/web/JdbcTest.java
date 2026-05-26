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
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
            new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                    null,
                    "name",
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
    void xaTxTesr() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserdataUserJson user = usersDbClient.createUserSpringJdbc(
            new UserdataUserJson(
                null,
                "user-2",
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
