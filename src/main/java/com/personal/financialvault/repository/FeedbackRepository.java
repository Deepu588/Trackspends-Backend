package com.personal.financialvault.repository;

import com.personal.financialvault.dto.response.FeedbackResponse;
import com.personal.financialvault.entity.FeedbackRating;
import com.personal.financialvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackRating,Long> {


    @Query("select new com.personal.financialvault.dto.response.FeedbackResponse(f.ratingStars,f.message) from FeedbackRating f where user=:user ")
    List<FeedbackResponse> getParticularUserFeedback(@Param("user") User user);


}
