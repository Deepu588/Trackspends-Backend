package com.personal.financialvault.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class MonthlySavingsRequest {

    @NotNull(message = "Month is required")
    //@Size(max = 12,min = 1)
    @Max(value = 12, message = "Month should not exceed than 12")
    @Min(value = 1,message = "Month should starts with 1")
    @Positive(message = "Expense Month should be positive")
    private Integer expenseMonth;

    @NotNull(message = "Year is required")
    @Positive(message = "Expense Year should be greater than zero")
    private Integer expenseYear;

    @NotNull(message = "salary is required")
    @Positive(message = "Salary should be greater than zero")
    private Double salary;
}
