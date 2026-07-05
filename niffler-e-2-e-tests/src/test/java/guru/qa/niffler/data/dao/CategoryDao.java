package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {
    CategoryEntity create(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);

    Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName);

    List<CategoryEntity> findByUsername(String username);

    List<CategoryEntity> findAll();

    CategoryEntity update(CategoryEntity category);

    void delete(CategoryEntity category);
}