package com.personal.financialvault.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Positive(message = "Age should be positive ")
    @Max(value = 100,message = "Max value for age is 100")
   @NotNull(message = "Age is required")
    private Integer age;

    @NotBlank(message = "Marital status is required")
    private String maritalStatus;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary should be greater than 0")
    private Double monthlySalary;

    @NotBlank(message = "Employment Domain is required")
    private String employmentDomain;
}
