package com.denchik.demo.repository;

import com.denchik.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryById(Integer id);
    Category findCategoryByName(String name);
    @Query("SELECT c FROM Category c INNER join fetch c.typeCategory as typeCategory where typeCategory.name_type= :name_type ")
    List<Category> findCategoriesByTypeCategoryByName (@Param("name_type") String name_type);
}
