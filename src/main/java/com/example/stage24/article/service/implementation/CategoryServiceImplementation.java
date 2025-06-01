package com.example.stage24.article.service.implementation;


import com.example.stage24.article.domain.Category;
import com.example.stage24.article.domain.Category;
import com.example.stage24.article.domain.Stock;
import com.example.stage24.article.model.request.NewCategory;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.repository.StockRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class CategoryServiceImplementation implements CategoryServiceInterface {

    private CategoryRepository categoryRepository;
    private SharedServiceInterface sharedService;


    @Override
    public Category addCategory(NewCategory category) {
        Category savedCategory = new Category();
        savedCategory.setName(category.getName());
        savedCategory.setDescription(category.getDescription());

        return categoryRepository.save(savedCategory);
    }

    @Override
    public Category updateCategory(Category category) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}

