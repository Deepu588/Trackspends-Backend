package com.personal.financialvault.controller;


import com.personal.financialvault.dto.request.AiAdvisorRequest;
import com.personal.financialvault.dto.request.AiAdvisorResponse;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.service.AiAdvisorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAdvisorController {


    private final AiAdvisorService aiAdvisorService;


    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<AiAdvisorResponse>> ask(@AuthenticationPrincipal UserDetails userDetails,
                                                              @Valid @RequestBody AiAdvisorRequest request
                                              ){

       return  ResponseEntity.status(201).body(aiAdvisorService.askAdvisor(request,userDetails));
    }

     @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<AiAdvisorResponse>>> getHistory(@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.status(200).body(aiAdvisorService.getHistory(userDetails));
    }


}
