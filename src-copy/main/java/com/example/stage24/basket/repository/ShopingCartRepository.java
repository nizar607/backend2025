package com.example.stage24.basket.repository;

import com.example.stage24.article.domain.Category;
import com.example.stage24.basket.domain.ShopingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopingCartRepository extends JpaRepository<ShopingCart, Long> {

}