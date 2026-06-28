package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());


    @Override
    public UserdataUserEntity create(UserdataUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UserdataUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserdataUserEntity.class, id));
    }

    @Override
    public Optional<UserdataUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                entityManager.createQuery("select u from UserdataUserEntity where u.username = :username", UserdataUserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void addOutcomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addIncomeInvitation(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

    @Override
    public void addFriend(UserdataUserEntity requester, UserdataUserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public List<UserdataUserEntity> findAll() {
        return List.of();
    }

    @Override
    public void delete(UserdataUserEntity user) {

    }
}
