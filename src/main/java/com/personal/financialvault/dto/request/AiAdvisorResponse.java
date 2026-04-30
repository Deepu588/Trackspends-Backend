package com.personal.financialvault.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAdvisorResponse {

    private Long insightId;
    private String userQuery;
    private String aiResponse;
    private LocalDateTime createdAt;
}
