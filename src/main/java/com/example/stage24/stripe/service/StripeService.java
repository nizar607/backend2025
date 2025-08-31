package com.example.stage24.stripe.service;

import com.example.stage24.stripe.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.stage24.basket.service.interfaces.ShopingCartServiceInterface;
import com.example.stage24.basket.domain.Item;
import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.article.domain.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import com.example.stage24.order.repository.OrderRepository;
// import com.example.stage24.invoice.repository.InvoiceRepository;
// import com.example.stage24.invoice.repository.InvoiceItemRepository;
import com.example.stage24.order.domain.Order;
// import com.example.stage24.invoice.domain.Invoice;
// import com.example.stage24.invoice.domain.InvoiceItem;
import com.example.stage24.user.domain.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeService {

        @Value("${stripe.secret.key}")
        private String secretKey;

        private final ShopingCartServiceInterface cartService;
        private final SharedServiceInterface sharedService;
        private final OrderRepository orderRepository;
        // private final InvoiceRepository invoiceRepository;
        // private final InvoiceItemRepository invoiceItemRepository;

        // stripe -API
        // -> build line items from user's cart
        // -> return sessionId and url

        @Transactional(readOnly = true)
        public StripeResponse checkoutProducts(String website) {
                // Set your secret key. Remember to switch to your live secret key in
                // production!
                Stripe.apiKey = secretKey;

                // Ensure user is authenticated
                sharedService.getConnectedUser()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "User not authenticated"));

                // Get user's cart and items
                ShopingCart cart = cartService.getOrCreateUserCart()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Cart not found"));

                List<Item> items = cart.getItems();

                if (items == null || items.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
                }

                // Create new session with the line items
                SessionCreateParams.Builder builder = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:4200/client/" + website + "/articles")
                                .setCancelUrl("http://localhost:4200/");

                // Build line items from cart
                for (Item item : items) {
                        if (item == null)
                                continue;
                        if (item.getQuantity() <= 0) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Cart item quantity must be at least 1");
                        }

                        Article article = item.getArticle();
                        if (article == null) {
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Cart item missing article");
                        }

                        long unitAmount = Math.round(article.getPrice() * 100); // convert to cents
                        if (unitAmount <= 0) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Article price must be greater than 0");
                        }

                        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                                        .builder()
                                        .setName(article.getName())
                                        .build();

                        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData
                                        .builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(unitAmount)
                                        .setProductData(productData)
                                        .build();

                        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                                        .setQuantity(Long.valueOf(item.getQuantity()))
                                        .setPriceData(priceData)
                                        .build();

                        builder.addLineItem(lineItem);
                }

                SessionCreateParams params = builder.build();

                // Create new session
                Session session;
                try {
                        session = Session.create(params);
                } catch (StripeException e) {
                        log.error("Stripe session creation failed", e);
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Stripe session creation failed");
                }
                return StripeResponse
                                .builder()
                                .status("SUCCESS")
                                .message("Payment session created ")
                                .sessionId(session.getId())
                                .sessionUrl(session.getUrl())
                                .build();
        }

        // Simple immediate invoice creation (no webhook): build Order + Invoice from current cart
        // @Transactional
        // public Invoice createSimpleInvoiceForCurrentCart() {
        //         User user = sharedService.getConnectedUser()
        //                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
        //                                         "User not authenticated"));

        //         ShopingCart cart = cartService.getOrCreateUserCart()
        //                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                                         "Cart not found"));

        //         List<Item> items = cart.getItems();
        //         if (items == null || items.isEmpty()) {
        //                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        //         }

        //         double subtotal = 0.0;
        //         for (Item item : items) {
        //                 Article article = item.getArticle();
        //                 if (article == null) {
        //                         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
        //                                         "Cart item missing article");
        //                 }
        //                 if (item.getQuantity() <= 0) {
        //                         throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                                         "Invalid item quantity");
        //                 }
        //                 subtotal += article.getPrice() * item.getQuantity();
        //         }
        //         double tax = 0.0;
        //         double shipping = 0.0;
        //         double total = subtotal + tax + shipping;

        //         // Create minimal Order snapshot (required by Invoice)
        //         Order order = new Order();
        //         order.setUser(user);
        //         order.generateOrderNumber();
        //         order.setOrderStatus(Order.OrderStatus.PENDING);
        //         order.setSubtotalAmount(subtotal);
        //         order.setTaxAmount(tax);
        //         order.setShippingAmount(shipping);
        //         order.setTotalAmount(total);
        //         order.setDiscountAmount(0.0);
        //         order.setPaymentMethod("STRIPE");
        //         order.setPaymentStatus(Order.PaymentStatus.PENDING);
        //         order.setShippingAddress("N/A"); // Required @NotBlank field
        //         order = orderRepository.save(order);

        //         // Create Invoice
        //         Invoice invoice = new Invoice();
        //         // invoice.setUser(user);
        //         invoice.generateInvoiceNumber();
        //         invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        //         invoice.setSubtotalAmount(subtotal);
        //         invoice.setTaxAmount(tax);
        //         invoice.setTotalAmount(total);
        //         invoice = invoiceRepository.save(invoice);

        //         // Create Invoice Items
        //         for (Item item : items) {
        //                 Article article = item.getArticle();
        //                 InvoiceItem invoiceItem = new InvoiceItem();
        //                 invoiceItem.setInvoice(invoice);
        //                 invoiceItem.setProductDescription(article.getDescription());
        //                 invoiceItem.setProductCategory(article.getCategory().getName());
        //                 invoiceItem.setProductImageUrl(article.getImage());

        //                 invoiceItem.setProductName(article.getName());
        //                 invoiceItem.setProductDescription(article.getDescription());
        //                 invoiceItem.setUnitPrice(article.getPrice());
        //                 invoiceItem.setQuantity(item.getQuantity());
        //                 invoiceItemRepository.save(invoiceItem);
        //         }

        //         log.info("Created simple invoice {} for user {} from current cart", invoice.getInvoiceNumber(),
        //                         user.getEmail());
        //         return invoice;
        // }

}
