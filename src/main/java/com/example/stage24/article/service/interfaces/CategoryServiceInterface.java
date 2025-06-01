package com.example.stage24.article.service.interfaces;


import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceInterface {

    public Category addCategory(NewCategory category);

    public Category updateCategory(Category category);

    public void deleteCategory(Long id);

    public Optional<Category> getCategory(Long id);

    public List<Category> getAllCategories();
}
