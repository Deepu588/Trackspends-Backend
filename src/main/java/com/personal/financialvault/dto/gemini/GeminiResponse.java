package com.personal.financialvault.dto.gemini;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    public String extractText() {
        try {
            return candidates.get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();
        } catch (Exception e) {
            return "Unable to get response from AI. Please try again.";
        }
    }
}