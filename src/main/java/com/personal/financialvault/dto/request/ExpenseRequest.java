package com.personal.financialvault.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
//@Builder
public class ExpenseRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is also required")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date is required")
    private LocalDate date;


}