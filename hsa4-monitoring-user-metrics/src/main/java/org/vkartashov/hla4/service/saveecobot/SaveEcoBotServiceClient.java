package org.vkartashov.hla4.service.saveecobot;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vkartashov.hla4.service.saveecobot.dto.EcoBotDataEntryDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SaveEcoBotServiceClient {

    public static final String SAVE_ECO_BOT_OUTPUT_URL = "https://api.saveecobot.com/output.json";

    private final RestTemplate restTemplate;

    public SaveEcoBotServiceClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Retryable(maxAttempts=5, value = RuntimeException.class,
            backoff = @Backoff(delay = 10000))
    public List<EcoBotDataEntryDto> fetchData() {
        ResponseEntity<EcoBotDataEntryDto[]> response = this.restTemplate.getForEntity(SAVE_ECO_BOT_OUTPUT_URL, EcoBotDataEntryDto[].class);
        return Optional.ofNullable(response.getBody()).map(Arrays::asList).orElse(Collections.emptyList());
    }

}