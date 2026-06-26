package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    private final SelenideElement myFriendsTitle = $$("div").findBy(text("My friends"));
    private final SelenideElement friendRequestsTitle = $$("div").findBy(text("Friend requests"));
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final ElementsCollection allPeopleTableRows = $$("#all tr");
    private final SelenideElement TextNoUsers = $$("p").findBy(text("There are no users yet"));
    private final SelenideElement allPeopleTab = $("a[href='/people/all']");

    public void checkFriendPresentInTable(String friendName) {
        myFriendsTitle.shouldBe(visible);
        friendsTableRows.shouldHave(sizeGreaterThan(0));
        friendRow(friendName).shouldBe(visible);
        friendRow(friendName).$("button").shouldHave(text("Unfriend"));
    }

    public FriendsPage checkFriendsTableIsEmpty() {
        myFriendsTitle.shouldNotBe(visible);
        TextNoUsers.shouldBe(visible);
        return this;
    }

    public void checkIncomeInvitationBePresent(String income) {
        friendRequestsTitle.shouldBe(visible);
        requestsTableRows.shouldHave(sizeGreaterThan(0));
        incomeRequestRow(income).shouldBe(visible);
        incomeRequestRow(income).$$("button")
            .findBy(text("Accept"))
            .shouldBe(visible);
        incomeRequestRow(income).$$("button")
            .findBy(text("Decline"))
            .shouldBe(visible);
    }

    public FriendsPage navigateToAllPeopleTab() {
        allPeopleTab.shouldBe(visible).click();
        return this;
    }

    public void checkOutcomeInvitationBePresent(String outcome) {
        allPeopleTableRows.shouldHave(sizeGreaterThan(0));
        outcomeRequestRow(outcome).shouldBe(visible);
        incomeRequestRow(outcome).$$("span")
            .findBy(text("Waiting..."))
            .shouldBe(visible);
    }

    private SelenideElement friendRow(String name) {
        return friendsTableRows.findBy(text(name));
    }

    private SelenideElement incomeRequestRow(String income) {
        return requestsTableRows.findBy(text(income));
    }

    private SelenideElement outcomeRequestRow(String outcome) {
        return allPeopleTableRows.findBy(text(outcome));
    }
}
