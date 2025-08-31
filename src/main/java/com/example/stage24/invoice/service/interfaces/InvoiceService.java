package com.example.stage24.invoice.service.interfaces;

import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.invoice.domain.InvoiceItem;
import com.example.stage24.invoice.model.requests.InvoiceCreateRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    
    // Basic CRUD Operations
    Invoice createInvoice(InvoiceCreateRequest invoice);
    Invoice updateInvoice(Long id, Invoice invoice);
    Optional<Invoice> getInvoiceById(Long id);
    Optional<Invoice> getInvoiceByNumber(String invoiceNumber);
    List<Invoice> getAllInvoices();
    Page<Invoice> getAllInvoices(Pageable pageable);
    void deleteInvoice(Long id);
    boolean existsById(Long id);
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    // Invoice Item Management
    Invoice addItemToInvoice(Long invoiceId, InvoiceItem item);
    Invoice removeItemFromInvoice(Long invoiceId, Long itemId);
    Invoice updateInvoiceItem(Long invoiceId, Long itemId, InvoiceItem updatedItem);
    List<InvoiceItem> getInvoiceItems(Long invoiceId);
    
    // Business Logic Operations
    Invoice calculateInvoiceTotals(Long invoiceId);
    String generateInvoiceNumber();
    Invoice markAsPaid(Long invoiceId);
    Invoice markAsCancelled(Long invoiceId);
    
    // Search and Filter Operations
    List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status);
    List<Invoice> getInvoicesByCustomerEmail(String customerEmail);
    List<Invoice> getInvoicesByCustomerName(String customerName);
        
}