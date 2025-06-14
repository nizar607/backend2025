package com.example.stage24.article.service.implementation;

import com.example.stage24.article.domain.Category;
import com.example.stage24.article.model.request.NewCategory;
import com.example.stage24.article.model.response.ResponseCategory;
import com.example.stage24.article.repository.CategoryRepository;
import com.example.stage24.article.service.interfaces.CategoryServiceInterface;
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

@Service
@AllArgsConstructor
public class CategoryServiceImplementation implements CategoryServiceInterface {

    private CategoryRepository categoryRepository;
    private SharedServiceInterface sharedService;
    private NotificationService notificationService;
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Category addCategory(NewCategory category) {
        Category savedCategory = new Category();
        savedCategory.setName(category.getName());
        savedCategory.setDescription(category.getDescription());

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
}
