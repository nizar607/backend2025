package com.example.stage24.basket.model.response;

import com.example.stage24.basket.domain.ShopingCart;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ResponseShopingCart {
    private Long id;
    private double subTotalAmount;
    private double taxAmount;
    private double totalAmount;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResponseItem> items;
    
    public ResponseShopingCart(ShopingCart cart) {
        this.id = cart.getId();
        this.subTotalAmount = cart.getSubTotalAmount();
        this.taxAmount = cart.getTaxAmount();
        this.totalAmount = cart.getTotalAmount();
        this.quantity = cart.getQuantity();
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();
        
        // Convert items to ResponseItem objects
        if (cart.getItems() != null) {
            this.items = cart.getItems().stream()
                    .map(ResponseItem::new)
                    .collect(Collectors.toList());
        }
    }
}