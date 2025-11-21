package com.healthcare.medVault.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.MultipartBodyBuilder;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    @Value("${summary.base-url:http://127.0.0.1:8001}")
    private String summaryBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/summarize")
    public ResponseEntity<?> summarize(@RequestParam("file") MultipartFile file) {
        try {
            String url = summaryBaseUrl;
            if (!url.endsWith("/")) url += "/";
            url += "summarize/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", contentsAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/extract_medicines")
    public ResponseEntity<?> extractMedicines(@RequestParam("file") MultipartFile file) {
        try {
            String url = summaryBaseUrl;
            if (!url.endsWith("/")) url += "/";
            url += "extract_medicines/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", contentsAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/summarize-all")
    public ResponseEntity<?> summarizeAll(@RequestParam("files") List<MultipartFile> files) {
        WebClient client = WebClient.create(summaryBaseUrl);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        for (MultipartFile file : files) {
            bodyBuilder.part("files", file.getResource());
        }

        var response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/summarize_all/").build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

}
