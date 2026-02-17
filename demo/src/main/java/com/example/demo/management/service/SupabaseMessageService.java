package com.example.demo.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseMessageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();
    }

    // INSERT MESSAGE
    public void insertMessage(Map<String, Object> payload) {
        webClient()
                .post()
                .uri("/rest/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Map<String, Object> getMessageById(UUID messageId) {
        List<Map<String, Object>> result = webClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/v1/messages")
                        .queryParam("id", "eq." + messageId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if (result == null || result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    // UPDATE MESSAGE (EDIT / SOFT DELETE)
    public void updateMessage(UUID messageId, Map<String, Object> payload) {
        webClient()
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/v1/messages")
                        .queryParam("id", "eq." + messageId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // HARD DELETE MESSAGE (NOT RECOMMENDED)
    public void deleteMessage(UUID messageId) {
        webClient()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/v1/messages")
                        .queryParam("id", "eq." + messageId)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}