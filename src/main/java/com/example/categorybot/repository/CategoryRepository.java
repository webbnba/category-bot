package com.example.categorybot.repository;

import com.example.categorybot.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий категорий
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Получить все категории-потомки
     *
     * @param parentId id родителя
     * @return
     */
    Optional<List<Category>> findAllByParentId(Long parentId);

    /**
     * Поиск минимального id корневой категории
     *
     * @return
     */
    @Query(nativeQuery = true, value = "SELECT MIN(parent_id) FROM categories")
    long findMinParent();

    /**
     * Поиск категории по имени
     *
     * @param categoryName
     * @return
     */
    Optional<Category> findByName(String categoryName);
}
