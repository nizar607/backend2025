package com.example.stage24.article.service.implementation;

import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewCategory;
import com.example.stage24.article.model.response.ResponseCategory;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
import com.example.stage24.company.model.Company;

import com.example.stage24.company.repository.CompanyRepository;
import com.example.stage24.notification.domain.Notification;
import com.example.stage24.notification.service.NotificationService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.websocket.model.Message;

import lombok.AllArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final SharedServiceInterface sharedService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CompanyRepository companyRepository;

    public CategoryServiceImplementation(CategoryRepository categoryRepository,
                                       SharedServiceInterface sharedService,
                                       NotificationService notificationService,
                                       SimpMessagingTemplate messagingTemplate,
                                       CompanyRepository companyRepository) {
        this.categoryRepository = categoryRepository;
        this.sharedService = sharedService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.companyRepository = companyRepository;
    }

    @Override
    public Category addCategory(NewCategory category) {
        // Get the current user's company
        User user = sharedService.getConnectedUser()
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = user.getCompany();
        if (company == null) {
            throw new RuntimeException("User must be associated with a company to add categories");
        }
        
        Category savedCategory = new Category();
        savedCategory.setName(category.getName());
        savedCategory.setDescription(category.getDescription());
        savedCategory.setCompany(company); // Associate category with company

        Category result = categoryRepository.save(savedCategory);

        try {
            // Create notification for broadcast
            Notification notification = new Notification();
            notification.setMessage("New category added: " + savedCategory.getName());
            notification.setType("CATEGORY_CREATED");
            notification.setTimestamp(new Date());

            // Send broadcast notification (this will handle all users)
            notificationService.sendBroadcastNotification(notification);

        } catch (Exception e) {
            // Log error but don't fail the category creation
            System.err.println("Error sending category notification: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Category updateCategory(long id, ResponseCategory category) {
        Category savedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        savedCategory.setName(category.getName());
        savedCategory.setDescription(category.getDescription());
        savedCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        Category savedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(savedCategory);

        
        try {
            // Create notification for broadcast
            Notification notification = new Notification();
            notification.setMessage("category deleted: " + savedCategory.getName());
            notification.setType("CATEGORY_DELETED");
            notification.setTimestamp(new Date());

            // Send broadcast notification (this will handle all users)
            notificationService.sendBroadcastNotification(notification);

        } catch (Exception e) {
            // Log error but don't fail the category creation
            System.err.println("Error sending category notification: " + e.getMessage());
        }

    }

    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<ResponseCategory> getCategoriesByWebsite(String website) {
        // Find company by website
        Optional<Company> companyOpt = companyRepository.findByWebsite(website);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Company not found for website: " + website);
        }

        Company company = companyOpt.get();
        
        // Find categories by company
        Optional<List<Category>> categoriesOpt = categoryRepository.findByCompany(company);
        List<Category> categories = categoriesOpt.orElse(List.of());
        
        // Convert to ResponseCategory objects
        return categories.stream()
                .map(this::convertToResponseCategory)
                .collect(Collectors.toList());
    }

    private ResponseCategory convertToResponseCategory(Category category) {
        ResponseCategory responseCategory = new ResponseCategory();
        responseCategory.setId(category.getId());
        responseCategory.setName(category.getName());
        responseCategory.setDescription(category.getDescription());
        responseCategory.setCreatedAt(category.getCreatedAt());
        responseCategory.setUpdatedAt(category.getUpdatedAt());
        return responseCategory;
    }
}
