package com.personal.financialvault.dto.response;

import com.personal.financialvault.entity.FeedbackRating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackResponse {

    @Min(value = 1)
    @Max(value = 5)
    @Positive(message = "Rating Should be Positive value")
    private Integer rating;

    @NotNull(message = "Message is also required")
    private String message;



    public FeedbackResponse(FeedbackRating rating){
        this.rating=rating.getRatingStars();
        this.message=rating.getMessage();

    }



}
