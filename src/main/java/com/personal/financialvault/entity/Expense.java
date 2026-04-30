package com.personal.financialvault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Expense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
  //  @JsonIgnore
    private User user;

    @Column(nullable = false)
    @NotNull(message = "Amount is required")
    private Double amount;

    @Column(nullable = false)
    @NotNull(message = "category is required")
    private String category;

    @Column(nullable = false)
    @NotBlank(message = "Description is also required")
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    private Integer expenseMonth;

    private Integer expenseYear;

    @PrePersist
    @PreUpdate
    private void setMonthAndYear() {
        if (date != null) {
            this.expenseMonth = date.getMonthValue();
            this.expenseYear = date.getYear();
        }
    }
}