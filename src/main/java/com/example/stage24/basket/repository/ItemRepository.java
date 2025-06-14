package com.example.stage24.basket.repository;

import com.example.stage24.article.domain.Article;
import com.example.stage24.basket.domain.ShopingCart;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.stage24.basket.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findByArticleAndShopingCart(Article article, ShopingCart shopingCart);
    
    List<Item> findByShopingCart(ShopingCart shopingCart);
    
}