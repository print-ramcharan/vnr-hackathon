package com.healthcare.medVault.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/inference")
@RequiredArgsConstructor
public class PredictionController {

    @Value("${model.base-url:http://127.0.0.1:8002}")
    private String modelBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody Map<String, Object> payload) {
        try {
            String url = modelBaseUrl;
            if (!url.endsWith("/")) url += "/";
            url += "predict";

            ResponseEntity<Map> response = restTemplate.postForEntity(url, payload, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
