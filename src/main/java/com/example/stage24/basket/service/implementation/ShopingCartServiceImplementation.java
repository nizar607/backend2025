package com.example.stage24.basket.service.implementation;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.basket.domain.Item;
import com.example.stage24.basket.repository.ShopingCartRepository;
import com.example.stage24.basket.repository.ItemRepository;
import com.example.stage24.basket.service.interfaces.ShopingCartServiceInterface;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ShopingCartServiceImplementation implements ShopingCartServiceInterface {

    private final ShopingCartRepository shopingCartRepository;
    private final ItemRepository itemRepository;
    private final ArticleRepository articleRepository;
    private final SharedServiceInterface sharedService;

    @Override
    public Optional<ShopingCart> addArticleToCart(Long articleId, int quantity) {
        try {
            // Get the current user
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            // Get or create user's shopping cart
            ShopingCart cart = getOrCreateUserCart().orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create cart"));

            // Get the article
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

            // Check if item already exists in cart
            Optional<Item> existingItem = itemRepository.findByArticleAndShopingCart(article, cart);

            if (existingItem.isPresent()) {
                // Update quantity if item already exists
                Item item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantity);
                item.setUpdatedAt(LocalDateTime.now());
                itemRepository.save(item);
            } else {
                // Create new item
                Item newItem = new Item();
                newItem.setQuantity(quantity);
                newItem.setArticle(article);
                newItem.setShopingCart(cart);
                newItem.setCreatedAt(LocalDateTime.now());
                newItem.setUpdatedAt(LocalDateTime.now());
                itemRepository.save(newItem);
            }

            // Update cart totals
            updateCartTotals(cart);

            return Optional.of(shopingCartRepository.save(cart));

        } catch (Exception e) {
            log.error("Failed to add article to cart: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ShopingCart> getOrCreateUserCart() {

        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            Optional<ShopingCart> existingCart = shopingCartRepository.findByUser(user);

            if (existingCart.isPresent()) {
                return existingCart;
            } else {
                // Create new cart
                ShopingCart newCart = new ShopingCart();
                newCart.setUser(user);
                newCart.setSubTotalAmount(0.0);
                newCart.setTaxAmount(0.0);
                newCart.setTotalAmount(0.0);
                newCart.setQuantity(0);
                newCart.setCreatedAt(LocalDateTime.now());
                newCart.setUpdatedAt(LocalDateTime.now());

                return Optional.of(shopingCartRepository.save(newCart));
            }
        } catch (Exception e) {
            log.error("Failed to get or create user cart: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Item>> getCartItems() {
        try {
            ShopingCart cart = getOrCreateUserCart().orElse(null);
            if (cart == null) {
                return Optional.empty();
            }

            List<Item> items = itemRepository.findByShopingCart(cart);
            return Optional.of(items);

        } catch (Exception e) {
            log.error("Failed to get cart items: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ShopingCart> removeItemFromCart(Long itemId) {
        try {
            log.info("Starting remove item operation for item ID: {}", itemId);
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            log.info("User authenticated: {}", user.getId());
            ShopingCart cart = shopingCartRepository.findByUser(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

            // Find and remove the item from the database first
            Item itemToRemove = cart.getItems().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart"));

            log.info("Deleting item from database: {}", itemId);
            itemRepository.delete(itemToRemove);
            
            // Remove the item from the cart's collection
            cart.getItems().removeIf(i -> i.getId().equals(itemId));

            log.info("Updating cart totals");
            updateCartTotals(cart);

            ShopingCart savedCart = shopingCartRepository.save(cart);
            log.info("Item removed and cart updated successfully");
            return Optional.of(savedCart);

        } catch (Exception e) {
            log.error("Failed to remove item from cart: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ShopingCart> removeItemByArticleId(Long articleId) {
        try {
            log.info("Starting remove item operation for article ID: {}", articleId);
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            log.info("User authenticated: {}", user.getId());
            ShopingCart cart = shopingCartRepository.findByUser(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

            // Get the article
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

            // Find the item by article and cart
            Optional<Item> itemToRemove = itemRepository.findByArticleAndShopingCart(article, cart);
            
            if (itemToRemove.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart");
            }

            log.info("Deleting item from database: {}", itemToRemove.get().getId());
            itemRepository.delete(itemToRemove.get());
            
            // Remove the item from the cart's collection
            cart.getItems().removeIf(i -> i.getArticle().getId().equals(articleId));

            log.info("Updating cart totals");
            updateCartTotals(cart);

            ShopingCart savedCart = shopingCartRepository.save(cart);
            log.info("Item removed and cart updated successfully");
            return Optional.of(savedCart);

        } catch (Exception e) {
            log.error("Failed to remove item from cart: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ShopingCart> updateItemQuantity(Long itemId, int quantity) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

            ShopingCart cart = item.getShopingCart();

            // Verify the cart belongs to the current user
            if (!cart.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            if (quantity <= 0) {
                itemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                item.setUpdatedAt(LocalDateTime.now());
                itemRepository.save(item);
            }

            updateCartTotals(cart);

            return Optional.of(shopingCartRepository.save(cart));

        } catch (Exception e) {
            log.error("Failed to update item quantity: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ShopingCart> clearCart() {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated"));

            ShopingCart cart = getOrCreateUserCart().orElse(null);
            if (cart == null) {
                return Optional.empty();
            }

            // Verify the cart belongs to the current user
            if (!cart.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            // Clear all items in the cart
            cart.getItems().clear();

            // Reset cart totals
            cart.setSubTotalAmount(0.0);
            cart.setTaxAmount(0.0);
            cart.setTotalAmount(0.0);
            cart.setQuantity(0);
            cart.setUpdatedAt(LocalDateTime.now());

            return Optional.of(shopingCartRepository.save(cart));

        } catch (Exception e) {
            log.error("Failed to clear cart: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void updateCartTotals(ShopingCart cart) {
        List<Item> items = itemRepository.findByShopingCart(cart);

        double subTotal = items.stream()
                .mapToDouble(item -> item.getArticle().getPrice() * item.getQuantity())
                .sum();

        int totalQuantity = items.stream()
                .mapToInt(Item::getQuantity)
                .sum();

        double taxAmount = subTotal * 0.1; // 10% tax
        double totalAmount = subTotal + taxAmount;

        cart.setSubTotalAmount(subTotal);
        cart.setTaxAmount(taxAmount);
        cart.setTotalAmount(totalAmount);
        cart.setQuantity(totalQuantity);
        cart.setUpdatedAt(LocalDateTime.now());
    }

}
