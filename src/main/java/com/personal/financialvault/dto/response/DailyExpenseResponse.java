package com.personal.financialvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyExpenseResponse {

    private LocalDate date;
    private Double totalAmountPerDay;

}
