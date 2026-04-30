package com.personal.financialvault.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpensePatchRequest {

    @Positive(message = "Amount must be positive")
    private Double amount;

    private String category;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;


}
