package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .navigateToFriendsPage()
                .checkFriendPresentInTable(user.friend());
    }

    @Test
    void friendTableShouldBeEmptyForNewUser(@UserType() StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .navigateToFriendsPage()
                .checkFriendsTableIsEmpty();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .navigateToFriendsPage()
                .checkIncomeInvitationBePresent(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .navigateToFriendsPage()
                .checkFriendsTableIsEmpty()
                .navigateToAllPeopleTab()
                .checkOutcomeInvitationBePresent(user.outcome());
    }

    @Test
    void checkTwoUsersWithOneType(@UserType() StaticUser firstUser, @UserType() StaticUser secondUser) {
        System.out.println(firstUser.username());
        System.out.println(secondUser.username());
    }

    @Test
    void checkTwoUsersWithDifferentType(@UserType() StaticUser firstUser, @UserType(WITH_FRIEND) StaticUser secondUser) {
        System.out.println(firstUser.username());
        System.out.println(secondUser.username());
    }
}
