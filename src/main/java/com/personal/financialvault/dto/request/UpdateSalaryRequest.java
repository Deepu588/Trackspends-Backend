package com.personal.financialvault.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateSalaryRequest {

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary should be positive")
    private Double salary;
}
