package com.denchik.demo.service;

import com.denchik.demo.model.Category;
import com.denchik.demo.repository.CategoryRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryService (CategoryRepository categoryRepository) {
        System.out.println("Внедряю зависимость Category Service");
        this.categoryRepository = categoryRepository;
    }
    @Transactional(readOnly = true)
    public Category findCategoryById (Integer id) {
        return categoryRepository.findCategoryById(id);
    }
    @Transactional(readOnly = true)
   public Category findByCategoryName (String name) {
        return categoryRepository.findCategoryByName(name);
    }
    @Transactional(readOnly = true)
    public List<Category> findAllCategories () {
        return categoryRepository.findAll();
    }
    @Transactional(readOnly = true)
    public void addCategory (Category category) {
        categoryRepository.save(category);
    }
    @Transactional(readOnly = true)
    public List<Category> findDistinctOperationCategoryByUserAndTypeOperationName(int user_id, String nameOperationType) {
        return categoryRepository.findDistinctOperationCategoryByUserAndTypeOperationName(user_id,nameOperationType);
    }
    @Transactional
    public List<Category> getExpenses () {
        return categoryRepository.findCategoriesByTypeCategoryByName("Витрати");
    }
    @Transactional
    public List<Category> getIncomes () {
        return categoryRepository.findCategoriesByTypeCategoryByName("Надходження");
    }

}
