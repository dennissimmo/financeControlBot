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
    @Query(value = "SELECT DISTINCT c from category c LEFT JOIN operation o on c.id = o.category_id LEFT JOIN users u on u.id = o.user_id LEFT JOIN type_operation t on t.id = o.type_operation_id WHERE u.id = ?1 AND t.name = ?2",nativeQuery = true)
    public List<Category> findDistinctOperationCategoryByUserAndTypeOperationName (int user_id, String nameOperationType);
    @Query(value = "SELECT DISTINCT c from category c LEFT JOIN operation o on c.id = o.category_id LEFT JOIN users u on u.id = o.user_id LEFT JOIN type_operation t on t.id = o.type_operation_id WHERE u.id = ?1",nativeQuery = true)
    public List<Category> findDistinctOperationCategoryByUser (int userId) ;
    @Query("SELECT c FROM Category c INNER join fetch c.typeCategory as typeCategory where typeCategory.name_type= :name_type and c.locale= :locale")
    List<Category> findCategoriesByTypeCategoryByNameAndLocale (@Param("name_type") String name_type,@Param("locale") String locale);
    @Query("SELECT c FROM Category c INNER join fetch c.typeCategory as typeCategory where typeCategory.name_type= :name_type")
    List<Category> findCategoriesByTypeCategoryByName (@Param("name_type") String name_type);
}
