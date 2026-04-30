package com.personal.financialvault.service;

import com.personal.financialvault.dto.gemini.GeminiRequest;
import com.personal.financialvault.dto.gemini.GeminiResponse;
import com.personal.financialvault.dto.request.AiAdvisorRequest;
import com.personal.financialvault.dto.request.AiAdvisorResponse;
import com.personal.financialvault.dto.response.ApiResponse;
import com.personal.financialvault.entity.AiAdvisorHistory;
import com.personal.financialvault.entity.Expense;
import com.personal.financialvault.entity.User;
import com.personal.financialvault.exceptions.ResourceNotFoundException;
import com.personal.financialvault.repository.AiAdvisorRepository;
import com.personal.financialvault.repository.ExpenseRepository;
import com.personal.financialvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import lombok.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class AiAdvisorService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final AiAdvisorRepository aiAdvisorRepository;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private static final int RAW_EXPENSE_THRESHOLD = 30;

    public ApiResponse<AiAdvisorResponse> askAdvisor(
            AiAdvisorRequest request, UserDetails userDetails) {

        // Step 1 — Get user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Step 2 — Get all expenses
        List<Expense> allExpenses = expenseRepository.findByUser(user);

        // Step 3 — Build prompt
        String prompt = buildPrompt(user, allExpenses, request.getUserQuestion());

        // Step 4 — Call Gemini
        String aiResponseText = callGemini(prompt);

        // Step 5 — Save to history
        AiAdvisorHistory history = AiAdvisorHistory.builder()
                .user(user)
                .userQuery(request.getUserQuestion())
                .aiResponse(aiResponseText)
                .build();
        AiAdvisorHistory saved = aiAdvisorRepository.save(history);

        // Step 6 — Return response
        return ApiResponse.<AiAdvisorResponse>builder()
                .success(true)
                .message("AI response generated")
                .data(AiAdvisorResponse.builder()
                        .insightId(saved.getInsightId())
                        .userQuery(saved.getUserQuery())
                        .aiResponse(saved.getAiResponse())
                        .createdAt(saved.getCreatedAt())
                        .build())
                .build();
    }

    // ─────────────────────────────────────────
    // Build Prompt — Smart adaptive logic
    // ─────────────────────────────────────────
    private String buildPrompt(User user, List<Expense> expenses, String question) {

        StringBuilder prompt = new StringBuilder();

        // User profile section
        prompt.append("You are a personal financial advisor.\n\n");
        prompt.append("User Profile:\n");
        prompt.append("- Age: ").append(user.getAge()).append("\n");
        prompt.append("- Marital Status: ").append(user.getMaritalStatus()).append("\n");
        prompt.append("- Monthly Salary: ₹").append(user.getMonthlySalary()).append("\n");
        prompt.append("- Employment Domain: ").append(user.getEmploymentDomain()).append("\n\n");

        // Adaptive expense section
        if (expenses.size() <= RAW_EXPENSE_THRESHOLD) {
            //  Less than or equal 30 → send raw expenses
            prompt.append("Expense Details (All Transactions):\n");
            for (Expense e : expenses) {
                prompt.append("- ")
                        .append(e.getDate()).append(" | ")
                        .append(e.getCategory()).append(" | ₹")
                        .append(e.getAmount()).append(" | ")
                        .append(e.getDescription()).append("\n");
            }
        } else {
            // More than 30 → send summarized data
            prompt.append("Expense Summary (Category-wise):\n");

            // Group by category and sum amounts
            Map<String, Double> categoryTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getCategory,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            double totalSpent = expenses.stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

            categoryTotals.forEach((category, total) ->
                    prompt.append("- ").append(category)
                            .append(": ₹").append(String.format("%.2f", total))
                            .append("\n")
            );

            prompt.append("- Total Spent: ₹")
                    .append(String.format("%.2f", totalSpent)).append("\n");

            if (user.getMonthlySalary() != null) {
                double remaining = user.getMonthlySalary() - totalSpent;
                prompt.append("- Remaining from Salary: ₹")
                        .append(String.format("%.2f", remaining)).append("\n");
            }
        }

        // User question
        prompt.append("\nUser Question: ").append(question).append("\n\n");

        // Response instruction
        prompt.append("Give practical, specific financial advice. ")
                .append("Respond in plain text only. ")
                .append("No JSON, no bullet symbols, no markdown. ")
                .append("Maximum 5 to 8  sentences.");

        return prompt.toString();
    }

    // ─────────────────────────────────────────
    // Call Gemini API
    // ─────────────────────────────────────────
    private String callGemini(String prompt) {
        try {
            String url = geminiApiUrl + "?key=" + geminiApiKey;

            // Build request body
            GeminiRequest geminiRequest = GeminiRequest.builder()
                    .contents(List.of(
                            GeminiRequest.Content.builder()
                                    .parts(List.of(
                                            GeminiRequest.Part.builder()
                                                    .text(prompt)
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            // Call Gemini
            GeminiResponse geminiResponse = restTemplate.postForObject(
                    url,
                    geminiRequest,
                    GeminiResponse.class
            );

            // Extract plain text
            if (geminiResponse != null) {
                return geminiResponse.extractText();
            }

            return "No response received from AI.";

        } catch (Exception e) {
            e.printStackTrace();
            return "AI service is temporarily unavailable. Please try again later.";
        }
    }

    // ─────────────────────────────────────────
    // Get AI History
    // ─────────────────────────────────────────
    public ApiResponse<List<AiAdvisorResponse>> getHistory(UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<AiAdvisorResponse> history = aiAdvisorRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(h -> AiAdvisorResponse.builder()
                        .insightId(h.getInsightId())
                        .userQuery(h.getUserQuery())
                        .aiResponse(h.getAiResponse())
                        .createdAt(h.getCreatedAt())
                        .build())
                .toList();

        return ApiResponse.<List<AiAdvisorResponse>>builder()
                .success(true)
                .message("History loaded successfully")
                .data(history)
                .build();
    }
}