package com.personal.financialvault.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiAdvisorRequest {

    @NotBlank(message = "Question is required")
    private String userQuestion;
}
