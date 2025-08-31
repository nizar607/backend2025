package com.example.stage24.invoice.model.responses;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceItemResponse {

    private Long id;
    private String articleName;
    private String articleDescription;
    private String articleCategory;
    private String articleImageUrl;
    
    private double unitPrice;
    private Integer quantity;

}
