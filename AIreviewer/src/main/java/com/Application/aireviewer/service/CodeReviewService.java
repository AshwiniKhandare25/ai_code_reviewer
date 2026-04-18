package com.Application.aireviewer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CodeReviewService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .build();

    public String reviewCode(String code) {

        // 🔹 Prompt
        String prompt = """
        Analyze the following code and respond in STRICT simple format:
        
        Bugs:
        - bullet points only
        
        Improvements:
        - bullet points only
        
        Suggestions:
        - bullet points only
        
        Score:
        - number out of 10
        
        Keep response SHORT and clear. No tables. No markdown.
        
        Code:
        """ + code;
        try {
            // 🔹 Correct request body
            String requestBody = String.format("""
            {
              "model": "openai/gpt-oss-120b:free",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """, prompt.replace("\"", "\\\"")); // escape quotes

            // 🔹 API call
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("HTTP-Referer", "http://localhost:8080")
                    .header("X-Title", "AI Code Reviewer")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);

            // Extract AI message
            String result = jsonNode
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

            result = result.replaceAll("\\n{3,}", "\n\n"); // remove too many blank lines

            return result;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}