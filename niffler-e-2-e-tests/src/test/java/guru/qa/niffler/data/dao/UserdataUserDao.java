package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.UserdataUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserdataUserEntity create(UserdataUserEntity user);

    Optional<UserdataUserEntity> findById(UUID id);

    Optional<UserdataUserEntity> findByUsername(String username);

    void delete(UserdataUserEntity user);
}
