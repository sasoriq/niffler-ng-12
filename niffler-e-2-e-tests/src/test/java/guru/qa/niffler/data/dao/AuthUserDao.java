package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    AuthUserEntity createUser(AuthUserEntity user);

    Optional<AuthUserEntity> findUserById(UUID id);

    List<AuthUserEntity> findAllByUsername(String username);

    List<AuthUserEntity> findAll();

    void deleteUser(AuthUserEntity user);
}
