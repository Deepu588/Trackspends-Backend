package com.personal.financialvault.dto.response;

import com.personal.financialvault.entity.Expense;
import lombok.Data;

import java.time.LocalDate;

@Data

public class ExpenseResponse {


    private Long expenseId;
    private Double amount;
    private String description;
    private String category;
    private LocalDate date;
    private int expenseMonth;
    private int expenseYear;
    private Long userId;

    public ExpenseResponse(Expense expense) {
        this.expenseId = expense.getExpenseId();
        this.amount = expense.getAmount();
        this.description =expense.getDescription();
        this.category = expense.getCategory();
        this.date = expense.getDate();
        this.expenseMonth = expense.getExpenseMonth();
        this.expenseYear = expense.getExpenseYear();
        this.userId = expense.getUser().getUserId();
    }
}
