package com.example.stage24.invoice.model.responses;

import java.util.List;


import lombok.Data;

@Data
public class InvoiceResponse {

    //user info
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userPhone;
    private String userAddress;

    //company info
    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private String companyPhone;
    private String companyWebsite;

    // price info
    private double subtotalAmount;
    private double taxAmount;
    private double totalAmount;
    private String currency;
    private double taxRate;

    // additional info
    private String footerText;

    // invoice items
    private List<InvoiceItemResponse> invoiceItems;
    
}
