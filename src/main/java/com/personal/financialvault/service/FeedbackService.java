package com.personal.financialvault.service;

import com.personal.financialvault.dto.request.FeedbackRequest;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.dto.response.FeedbackResponse;
import com.personal.financialvault.entity.FeedbackRating;
import com.personal.financialvault.entity.User;
import com.personal.financialvault.exceptions.ResourceNotFoundException;
import com.personal.financialvault.repository.FeedbackRepository;
import com.personal.financialvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FeedbackService {


    private final UserRepository userRepository;

    private final FeedbackRepository feedbackRepository;




    public ApiResponse<?> saveUserFeedback(@AuthenticationPrincipal UserDetails userDetails, FeedbackRequest request){

       User user= userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not Found"));

            FeedbackRating rating=FeedbackRating
                    .builder()
                    .ratingStars(request.getRatingStars())
                    .message(request.getMessage())
                    .user(user)
                    .build();

                 FeedbackRating savedRating=   feedbackRepository.save(rating);

                        return ApiResponse
                               .builder()
                                .success(true)
                                .message("Feedback Submitted Successfully !!")
                                .data(new FeedbackResponse(savedRating))
                                .build();



    }



    public ApiResponse<?> getUserFeedback(@AuthenticationPrincipal UserDetails userDetails){
      User user=  userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
      List<FeedbackResponse> feedbacks= feedbackRepository.getParticularUserFeedback(user);

      return ApiResponse
              .builder()
              .success(true)
              .message("Extracted the user Feedback successfully !! ")
              .data(feedbacks)
              .build();


    }





}
