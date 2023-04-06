package com.kvitka.gridnode.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PostStartConfiguration {

    @Value("${execution-properties.manager-url}")
    private String managerUrl;

    private final RestTemplate restTemplate;
    private final String nodeContextPath;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.put(managerUrl + "/grid-server/register",
                    new HttpEntity<>(Map.of("url", nodeContextPath), headers));

        } catch (RestClientException e) {
            log.error(e.getMessage());
        }
    }
}
