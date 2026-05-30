package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    UserdataUserEntity create(UserdataUserEntity user);

    Optional<UserdataUserEntity> findById(UUID id);

    Optional<UserdataUserEntity> findByUsername(String username);

    void addOutcomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee);

    void addIncomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee);

    void addFriend(UserdataUserEntity requester, UserdataUserEntity addressee);

    List<UserdataUserEntity> findAll();

    void delete(UserdataUserEntity user);
}
