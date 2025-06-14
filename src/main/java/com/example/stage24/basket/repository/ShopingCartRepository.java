package com.example.stage24.basket.repository;

import com.example.stage24.article.domain.Category;
import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShopingCartRepository extends JpaRepository<ShopingCart, Long> {
    
    Optional<ShopingCart> findByUser(User user);
    
}