package com.example.stage24.basket.repository;

import com.example.stage24.article.domain.Article;
import com.example.stage24.basket.domain.ShopingCart;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.stage24.basket.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findByArticleAndShopingCart(Article article, ShopingCart shopingCart);
    
    List<Item> findByShopingCart(ShopingCart shopingCart);
    
    @Query("SELECT i.article.id FROM Item i WHERE i.shopingCart.user.id = :userId")
    Set<Long> findArticleIdsByUserId(@Param("userId") Long userId);
    
}