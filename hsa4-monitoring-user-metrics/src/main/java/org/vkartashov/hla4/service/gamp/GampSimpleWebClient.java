package org.vkartashov.hla4.service.gamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.vkartashov.hla4.service.gamp.dto.GampCollectRequestDto;

@Service
public class GampSimpleWebClient {

    public static final String GA_URL = "https://www.google-analytics.com";

    private final RestTemplate restTemplate;

    @Value("${gamp.api.secret}")
    private String apiSecret;

    @Value("${gamp.api.measurementId}")
    private String measurementId;

    public GampSimpleWebClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Retryable(maxAttempts=5, value = RuntimeException.class,
            backoff = @Backoff(delay = 10000))
    public void publishCollectionRequest(GampCollectRequestDto event) {
        String url = UriComponentsBuilder.fromHttpUrl(GA_URL)
                .pathSegment("mp", "collect")
                .queryParam("api_secret", apiSecret)
                .queryParam("measurement_id", measurementId)
                .toUriString();
        restTemplate.postForEntity(url, event, String.class);
    }

}
