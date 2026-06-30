package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity create(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        entityManager.joinTransaction();
        return entityManager.merge(spend);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public List<SpendEntity> findByUsername(String username) {
        return entityManager.createQuery("select s from SpendEntity where s.username = :username",
                SpendEntity.class)
            .setParameter("username", username)
            .getResultList();
    }

    @Override
    public List<SpendEntity> findAll() {
        return entityManager.createQuery("select s from SpendEntity s",
                SpendEntity.class)
            .getResultList();
    }

    @Override
    public void delete(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        try {
            return Optional.of(
            entityManager.createQuery("select c from CategoryEntity c where c.username = :username and c.name = :name",
                CategoryEntity.class)
                .setParameter("username", username)
                .setParameter("name", name)
                .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CategoryEntity> findCategoryByUsername(String username) {
        return entityManager.createQuery("select c from CategoryEntity c where c.username = :username",
                CategoryEntity.class)
            .setParameter("username", username)
            .getResultList();
    }

    @Override
    public List<CategoryEntity> findAllCategories() {
        return entityManager
            .createQuery("select c from CategoryEntity c", CategoryEntity.class)
            .getResultList();
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        return entityManager.merge(category);
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.remove(category);
    }
}
