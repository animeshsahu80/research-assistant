package com.research.assistant;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;

    public ResearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public  String processContent(ResearchRequest researchRequest){
        String prompt=buildPrompt(researchRequest);
        System.out.println("url + api"+geminiApiUrl+geminiApiKey);
        Map<String , Object> payload=buildPayload(prompt);
        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();


        return extractTextFromResponse(response);
    }
    private String extractTextFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            // Navigate to "candidates" -> first element -> "content" -> "parts" -> first element -> "text"
            JsonNode textNode = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            return textNode.isMissingNode() ? "" : textNode.asText(); // Return extracted text or empty string if missing
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private Map<String , Object> buildPayload(String prompt){
        try {

            Map<String, Object> payload = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));

            payload.put("contents", Collections.singletonList(content));

            // Convert to JSON
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
            return  Collections.emptyMap(); // Return empty JSON in case of error
        }
    }
    private String buildPrompt(ResearchRequest researchRequest){
        StringBuilder prompt= new StringBuilder();

        switch (researchRequest.getOperation()){
            case "eli5":
                prompt.append("Explain the following text to me like i am 5 year old: \n\n");
            case "paraphrase":
                prompt.append("Paraphrase the following text: \n\n");
                break;
            case "summarize":
                prompt.append("Provide clear and concise summary of following text in a few sentences:\n\n");
                break;
            case "suggest_articles":
                prompt.append("based on following text suggest few more articles related to this text, format the response with bullet points and clear heading:\n\n");
            default:
                throw new IllegalArgumentException("Unknown operation: "+ researchRequest.getOperation());
        }

        prompt.append(researchRequest.getContent());
        return prompt.toString();
    }

}
