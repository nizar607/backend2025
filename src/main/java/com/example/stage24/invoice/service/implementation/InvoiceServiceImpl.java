package com.example.stage24.invoice.service.implementation;

import com.example.stage24.article.domain.Article;
import com.example.stage24.article.repository.ArticleRepository;
import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.invoice.domain.InvoiceItem;
import com.example.stage24.invoice.model.requests.InvoiceCreateRequest;
import com.example.stage24.invoice.repository.InvoiceRepository;
import com.example.stage24.invoice.repository.InvoiceItemRepository;
import com.example.stage24.invoice.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.cfg.defs.ru.INNDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ArticleRepository articleRepository;

    // Basic CRUD Operations
    @Override
    @Transactional
    public Invoice createInvoice(InvoiceCreateRequest invoiceRequest) {

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setUserFirstName(invoiceRequest.getUserFirstName());
        invoice.setUserLastName(invoiceRequest.getUserLastName());
        invoice.setUserEmail(invoiceRequest.getUserEmail());
        invoice.setUserPhone(invoiceRequest.getUserPhone());
        invoice.setUserAddress(invoiceRequest.getUserAddress());
        invoice.setCompanyName(invoiceRequest.getCompanyName());
        invoice.setCompanyAddress(invoiceRequest.getCompanyAddress());
        invoice.setCompanyEmail(invoiceRequest.getCompanyEmail());
        invoice.setCompanyPhone(invoiceRequest.getCompanyPhone());
        invoice.setCompanyWebsite(invoiceRequest.getCompanyWebsite());
        invoice.setTaxRate(invoiceRequest.getTaxRate());
        invoice.setCurrency(invoiceRequest.getCurrency());
        invoice.setFooterText(invoiceRequest.getFooterText());
        invoice.setSubtotalAmount(invoiceRequest.getSubtotalAmount());
        invoice.setTaxAmount(invoiceRequest.getTaxAmount());
        invoice.setTotalAmount(invoiceRequest.getTotalAmount());

        invoiceRequest.getInvoiceItems().forEach((item) -> {
            Article article = articleRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Article not found with ID: " + item.getId()));
            invoice.addInvoiceItem(convertArticleToInvoiceItem(article, item.getQuantity()));
        });

        // System.out.println(invoice);
        // Calculate totals
        // invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with ID: {} and number: {}",
                savedInvoice.getId(), savedInvoice.getInvoiceNumber());

        return savedInvoice;
    }

    @Override
    public Invoice updateInvoice(Long id, Invoice invoice) {
        log.info("Updating invoice with ID: {}", id);

        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));

        existingInvoice.setCompanyName(invoice.getCompanyName());
        existingInvoice.setCompanyAddress(invoice.getCompanyAddress());
        existingInvoice.setCompanyEmail(invoice.getCompanyEmail());
        existingInvoice.setCompanyPhone(invoice.getCompanyPhone());
        existingInvoice.setCompanyWebsite(invoice.getCompanyWebsite());

        existingInvoice.setTaxRate(invoice.getTaxRate());
        existingInvoice.setCurrency(invoice.getCurrency());
        existingInvoice.setFooterText(invoice.getFooterText());

        // Recalculate totals
        existingInvoice.calculateTotals();

        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        log.info("Invoice updated successfully with ID: {}", updatedInvoice.getId());

        return updatedInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public void deleteInvoice(Long id) {
        log.info("Deleting invoice with ID: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));

        // Check if invoice can be deleted
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot delete paid invoice");
        }

        invoiceRepository.deleteById(id);
        log.info("Invoice deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return invoiceRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.existsByInvoiceNumber(invoiceNumber);
    }

    // Invoice Item Management
    @Override
    public Invoice addItemToInvoice(Long invoiceId, InvoiceItem item) {
        log.info("Adding item to invoice with ID: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        item.setInvoice(invoice);
        item.calculateLineTotal();

        invoice.addInvoiceItem(item);
        invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Item added successfully to invoice with ID: {}", invoiceId);

        return savedInvoice;
    }

    @Override
    public Invoice removeItemFromInvoice(Long invoiceId, Long itemId) {
        log.info("Removing item {} from invoice with ID: {}", itemId, invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        InvoiceItem item = invoiceItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Invoice item not found with ID: " + itemId));

        invoice.removeInvoiceItem(item);
        invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Item removed successfully from invoice with ID: {}", invoiceId);

        return savedInvoice;
    }

    @Override
    public Invoice updateInvoiceItem(Long invoiceId, Long itemId, InvoiceItem updatedItem) {
        log.info("Updating item {} in invoice with ID: {}", itemId, invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        InvoiceItem existingItem = invoiceItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Invoice item not found with ID: " + itemId));

        // Update item fields
        existingItem.setArticleName(updatedItem.getArticleName());
        existingItem.setArticleDescription(updatedItem.getArticleDescription());
        existingItem.setUnitPrice(updatedItem.getUnitPrice());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setTaxRate(updatedItem.getTaxRate());

        existingItem.calculateLineTotal();
        invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Item updated successfully in invoice with ID: {}", invoiceId);

        return savedInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceItem> getInvoiceItems(Long invoiceId) {
        return invoiceItemRepository.findByInvoiceIdOrderByCreatedAtAsc(invoiceId);
    }

    /**
     * Converts an Article entity to an InvoiceItem entity
     * 
     * @param article  The Article to convert
     * @param quantity The quantity for the invoice item
     * @return InvoiceItem created from the Article
     */
    public InvoiceItem convertArticleToInvoiceItem(Article article, Integer quantity) {
        if (article == null) {
            throw new IllegalArgumentException("Article cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        log.info("Converting article with ID: {} to InvoiceItem", article.getId());

        InvoiceItem invoiceItem = new InvoiceItem();

        // Set article reference
        invoiceItem.setArticleId(article.getId());

        // Set product details from article
        invoiceItem.setArticleName(article.getName());
        invoiceItem.setArticleDescription(article.getDescription());
        invoiceItem.setUnitPrice(article.getPrice());
        invoiceItem.setQuantity(quantity);

        // Set category information if available
        if (article.getCategory() != null) {
            invoiceItem.setArticleCategory(article.getCategory().getName());
        }

        // Set image URL if available
        if (article.getImage() != null && !article.getImage().isEmpty()) {
            invoiceItem.setArticleImageUrl(article.getImage());
        }

        // Calculate line total
        invoiceItem.calculateLineTotal();

        log.info("Article converted to InvoiceItem successfully. Product: {}, Unit Price: {}, Quantity: {}",
                invoiceItem.getArticleName(), invoiceItem.getUnitPrice(), invoiceItem.getQuantity());

        return invoiceItem;
    }

    // Business Logic Operations
    @Override
    public Invoice calculateInvoiceTotals(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        invoice.calculateTotals();
        return invoiceRepository.save(invoice);
    }

    @Override
    public String generateInvoiceNumber() {
        // Generate a UUID-based invoice number
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Create invoice number with prefix and shortened UUID (first 8 characters)
        String invoiceNumber = "INV-" + uuid.substring(0, 8);

        return invoiceNumber;
    }

    @Override
    public Invoice markAsCancelled(Long invoiceId) {
        log.info("Marking invoice as cancelled with ID: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice marked as cancelled successfully with ID: {}", invoiceId);

        return savedInvoice;
    }

    @Override
    public Invoice markAsPaid(Long invoiceId) {
        log.info("Marking invoice as paid with ID: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        invoice.setStatus(Invoice.InvoiceStatus.PAID);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice marked as paid successfully with ID: {}", invoiceId);

        return savedInvoice;
    }

    // Search and Filter Operations
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomerEmail(String customerEmail) {
        return invoiceRepository.findByUserEmailOrderByCreatedAtDesc(customerEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomerName(String customerName) {
        return invoiceRepository.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(customerName,
                customerName);
    }

}