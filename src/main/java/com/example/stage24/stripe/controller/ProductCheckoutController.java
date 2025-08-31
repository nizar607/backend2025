package com.example.stage24.stripe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stage24.basket.service.interfaces.ShopingCartServiceInterface;
import com.example.stage24.stripe.dto.StripeResponse;
import com.example.stage24.stripe.service.StripeService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/product/v1")
@AllArgsConstructor
public class ProductCheckoutController {

    private StripeService stripeService;
    private final ShopingCartServiceInterface shopingCartService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestParam String website) {

        StripeResponse stripeResponse = stripeService.checkoutProducts(website);
        // Immediately create a simple invoice from the current cart (no webhook)
        // stripeService.createSimpleInvoiceForCurrentCart();
        // Now clear the cart
        shopingCartService.clearCart();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @GetMapping("/stripe-get")
    public String getMethodName(@RequestParam String param) {
        return "hello dog";
    }



}
