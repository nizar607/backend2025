package com.example.stage24.basket.service.interfaces;

import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.basket.domain.Item;
import java.util.Optional;
import java.util.List;

public interface ShopingCartServiceInterface {
    
    Optional<ShopingCart> addArticleToCart(Long articleId, int quantity);
    
    Optional<ShopingCart> getOrCreateUserCart();
    
    Optional<List<Item>> getCartItems();
    
    Optional<ShopingCart> removeItemFromCart(Long itemId);
    
    Optional<ShopingCart> updateItemQuantity(Long itemId, int quantity);
    
    Optional<ShopingCart> clearCart();
    
}
