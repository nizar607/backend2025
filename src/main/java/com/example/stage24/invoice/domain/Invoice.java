package com.example.stage24.invoice.domain;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyAddress;

    private String companyEmail;

    private String companyPhoneNumber;

    @NotBlank
    private String invoiceNumber;

    private LocalDateTime creationDate = LocalDateTime.now();

    @NotBlank
    private LocalDate dueDate = LocalDate.now().minusDays(10);

    @NotBlank
    private String status;

    @NotBlank
    private double totalAmount;

    private List<InvoiceServices> invoiceServices;




    public Invoice(String companyAddress, String companyEmail, String companyPhoneNumber, String invoiceNumber, String status, double totalAmount, List<InvoiceServices> invoiceServices) {
        this.companyAddress = companyAddress;
        this.companyEmail = companyEmail;
        this.companyPhoneNumber = companyPhoneNumber;
        this.invoiceNumber = invoiceNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.invoiceServices = invoiceServices;
    }

}