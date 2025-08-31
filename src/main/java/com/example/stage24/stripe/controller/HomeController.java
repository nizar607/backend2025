package com.example.stage24.stripe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.stage24.basket.service.interfaces.ShopingCartServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ShopingCartServiceInterface shopingCartService;

    @GetMapping
    public String index(){
        return "index";
    }

    @GetMapping("/success")
    public String success(){
        try {
            shopingCartService.clearCart();
        } catch (Exception e) {
            log.warn("Could not clear cart on success: {}", e.getMessage());
        }
        return "success";
    }
}
