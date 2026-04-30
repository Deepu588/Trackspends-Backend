package com.personal.financialvault.controller;


import com.personal.financialvault.dto.request.FeedbackRequest;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.repository.UserRepository;
import com.personal.financialvault.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> saveTheFeedback(@Valid @RequestBody FeedbackRequest request,
                                                          @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.status(201).body(feedbackService.saveUserFeedback(userDetails,request));

    }

    @GetMapping("/getAllFeedbacks")
    public ResponseEntity<ApiResponse<?>> getUserFeedback(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(200).body(feedbackService.getUserFeedback(userDetails));
    }

}
