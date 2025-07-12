package com.example.stage24.basket.controller;

import com.example.stage24.basket.domain.Item;
import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.basket.model.response.ResponseItem;
import com.example.stage24.basket.model.response.ResponseShopingCart;
import com.example.stage24.basket.service.interfaces.ShopingCartServiceInterface;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
@Slf4j
public class ShopingCartController {

    private final ShopingCartServiceInterface shopingCartService;

    @PostMapping("/add/{articleId}")
    public ResponseEntity<ResponseShopingCart> addArticleToCart(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") int quantity) {
        log.info("Adding article {} to cart with quantity: {}", articleId, quantity);
        
        if (quantity <= 0) {
            log.warn("Invalid quantity: {}", quantity);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        Optional<ShopingCart> updatedCart = shopingCartService.addArticleToCart(articleId, quantity);
        
        return updatedCart
                .map(cart -> ResponseEntity.status(HttpStatus.CREATED).body(new ResponseShopingCart(cart)))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping
    public ResponseEntity<ResponseShopingCart> getUserCart() {
        log.info("Fetching user's shopping cart");
        
        Optional<ShopingCart> cart = shopingCartService.getOrCreateUserCart();
        return cart
                .map(c -> ResponseEntity.ok(new ResponseShopingCart(c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/remove/article/{articleId}")
    public ResponseEntity<ResponseShopingCart> removeItemByArticleId(@PathVariable Long articleId) {
        log.info("Removing item with article ID: {} from cart", articleId);
        
        Optional<ShopingCart> updatedCart = shopingCartService.removeItemByArticleId(articleId);
        
        return updatedCart
                .map(cart -> ResponseEntity.ok(new ResponseShopingCart(cart)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/items")
    public ResponseEntity<List<ResponseItem>> getCartItems() {
        log.info("Fetching cart items");
        Optional<List<Item>> items = shopingCartService.getCartItems();
        return items
                .map(itemList -> {
                    List<ResponseItem> responseItems = itemList.stream()
                            .map(ResponseItem::new)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(responseItems);
                })
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<ResponseShopingCart> removeItemFromCart(@PathVariable Long itemId) {
        log.info("Removing item {} from cart", itemId);
        Optional<ShopingCart> updatedCart = shopingCartService.removeItemFromCart(itemId);
        return updatedCart
                .map(cart -> ResponseEntity.ok(new ResponseShopingCart(cart)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<ResponseShopingCart> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        log.info("Updating item {} quantity to: {}", itemId, quantity);
        
        if (quantity < 0) {
            log.warn("Invalid quantity: {}", quantity);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        Optional<ShopingCart> updatedCart = shopingCartService.updateItemQuantity(itemId, quantity);
        return updatedCart
                .map(cart -> ResponseEntity.ok(new ResponseShopingCart(cart)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<ResponseShopingCart> clearCart() {
        Optional<ShopingCart> clearedCart = shopingCartService.clearCart();
        return clearedCart.map(cart -> ResponseEntity.ok().body(new ResponseShopingCart(cart)))
                .orElse(ResponseEntity.notFound().build());
    }
}
