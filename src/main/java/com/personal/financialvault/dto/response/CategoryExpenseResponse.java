package com.personal.financialvault.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryExpenseResponse {


    private String category;
    private Double amount;
}
