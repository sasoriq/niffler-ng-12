package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
    SpendEntity create(SpendEntity spend);

    SpendEntity update(SpendEntity spend);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findByUsername(String username);

    List<SpendEntity> findAll();

    void delete(SpendEntity spend);

    CategoryEntity createCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name);

    List<CategoryEntity> findCategoryByUsername(String username);

    List<CategoryEntity> findAllCategories();

    CategoryEntity updateCategory(CategoryEntity category);

    void deleteCategory(CategoryEntity category);
}