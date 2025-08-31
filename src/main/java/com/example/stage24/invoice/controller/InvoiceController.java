package com.example.stage24.invoice.controller;

import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.invoice.domain.InvoiceItem;
import com.example.stage24.invoice.model.requests.InvoiceCreateRequest;
import com.example.stage24.invoice.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Basic CRUD Operations
    
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Invoice> invoicePage = invoiceService.getAllInvoices(pageable);
            return ResponseEntity.ok(invoicePage.getContent());
        } catch (Exception e) {
            log.error("Error retrieving invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
            return invoice.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        try {
            Optional<Invoice> invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
            return invoice.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving invoice with number: {}", invoiceNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceCreateRequest invoice) {
        try {
            Invoice createdInvoice = invoiceService.createInvoice(invoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        try {
            Invoice updatedInvoice = invoiceService.updateInvoice(id, invoice);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error updating invoice with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting invoice with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Invoice Item Management
    
    @GetMapping("/{id}/items")
    public ResponseEntity<List<InvoiceItem>> getInvoiceItems(@PathVariable Long id) {
        try {
            List<InvoiceItem> items = invoiceService.getInvoiceItems(id);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error retrieving items for invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Invoice> addItemToInvoice(@PathVariable Long id, @RequestBody InvoiceItem item) {
        try {
            Invoice updatedInvoice = invoiceService.addItemToInvoice(id, item);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error adding item to invoice with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error adding item to invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<Invoice> updateInvoiceItem(@PathVariable Long id, @PathVariable Long itemId, @RequestBody InvoiceItem item) {
        try {
            Invoice updatedInvoice = invoiceService.updateInvoiceItem(id, itemId, item);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error updating item {} in invoice with ID: {}", itemId, id, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating item {} in invoice with ID: {}", itemId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Invoice> removeItemFromInvoice(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            Invoice updatedInvoice = invoiceService.removeItemFromInvoice(id, itemId);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error removing item {} from invoice with ID: {}", itemId, id, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error removing item {} from invoice with ID: {}", itemId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Business Logic Operations
    
    @PutMapping("/{id}/calculate-totals")
    public ResponseEntity<Invoice> calculateInvoiceTotals(@PathVariable Long id) {
        try {
            Invoice updatedInvoice = invoiceService.calculateInvoiceTotals(id);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error calculating totals for invoice with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error calculating totals for invoice with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id) {
        try {
            Invoice updatedInvoice = invoiceService.markAsPaid(id);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error marking invoice as paid with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error marking invoice as paid with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Invoice> markAsCancelled(@PathVariable Long id) {
        try {
            Invoice updatedInvoice = invoiceService.markAsCancelled(id);
            return ResponseEntity.ok(updatedInvoice);
        } catch (RuntimeException e) {
            log.error("Error marking invoice as cancelled with ID: {}", id, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error marking invoice as cancelled with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search and Filter Operations
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        try {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByStatus(invoiceStatus);
            return ResponseEntity.ok(invoices);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: {}", status, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving invoices by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/email/{customerEmail}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomerEmail(@PathVariable String customerEmail) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByCustomerEmail(customerEmail);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving invoices for customer: {}", customerEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/name/{customerName}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomerName(@PathVariable String customerName) {
        try {
            List<Invoice> invoices = invoiceService.getInvoicesByCustomerName(customerName);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving invoices for customer name: {}", customerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Utility Operations
    
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkInvoiceExists(@PathVariable Long id) {
        try {
            boolean exists = invoiceService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking if invoice exists with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/exists/number/{invoiceNumber}")
    public ResponseEntity<Boolean> checkInvoiceNumberExists(@PathVariable String invoiceNumber) {
        try {
            boolean exists = invoiceService.existsByInvoiceNumber(invoiceNumber);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking if invoice number exists: {}", invoiceNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}