package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @User(
      username = "cat",
      categories = @Category(
          archived = true
      ),
      spendings = @Spending(
          amount = 1000
      )
  )
  void spendingDescriptionShouldBeEditedByTableAction(SpendJson spendJson) {
    final String newDescription = "abc";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login(spendJson.username(), "12345")
        .openSpendingByDescription(spendJson.description())
        .editSpendingDescription(newDescription)
        .save()
        .checkThatTableContainsSpending(newDescription);
  }
}
