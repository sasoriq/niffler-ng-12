package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.UserdataEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserdataEntity createUser(UserdataEntity user);

    Optional<UserdataEntity> findById(UUID id);

    Optional<UserdataEntity> findByUsername(String username);

    void delete(UserdataEntity user);
}
