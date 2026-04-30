package com.personal.financialvault.repository;

import com.personal.financialvault.entity.AiAdvisorHistory;
import com.personal.financialvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AiAdvisorRepository extends JpaRepository<AiAdvisorHistory,Long> {
    List<AiAdvisorHistory> findByUserOrderByCreatedAtDesc(User user);

}
