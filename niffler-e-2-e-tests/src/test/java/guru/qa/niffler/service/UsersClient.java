package guru.qa.niffler.service;

import guru.qa.niffler.model.UserdataUserJson;

public interface UsersClient {
    UserdataUserJson createUser(String username);

    void addIncomeInvitation(UserdataUserJson targetUser, int count);

    void addOutcomeInvitation(UserdataUserJson targetUser, int count);

    void addFriend(UserdataUserJson targetUser, int count);
}
