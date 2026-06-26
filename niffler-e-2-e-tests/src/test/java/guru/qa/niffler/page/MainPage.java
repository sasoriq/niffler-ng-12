package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final ElementsCollection tableRows = $("table tbody").$$("tr");
    private final SelenideElement friendsLink = $("a[href='/people/friends']");
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");

    public EditSpendingPage openSpendingByDescription(String description) {
        tableRows.find(text(description))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContainsSpending(String description) {
        tableRows.find(text(description))
                .should(visible);
        return this;
    }

    public FriendsPage navigateToFriendsPage() {
        menuBtn.shouldBe(visible)
                .click();
        friendsLink.shouldBe(visible)
                .shouldHave(text("Friends"));
        friendsLink.click();
        return new FriendsPage();
    }
}
