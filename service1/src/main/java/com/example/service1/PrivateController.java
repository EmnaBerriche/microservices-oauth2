package com.example.service1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PrivateController {

    private static final Logger logger = LoggerFactory.getLogger(PrivateController.class);

    @Value("${auth.token-uri}")
    private String tokenUri;

    @Value("${auth.client-id}")
    private String clientId;

    @Value("${auth.client-secret}")
    private String clientSecret;

    @Value("${auth.service2-url}")
    private String service2Url;

    @GetMapping("/private")
    public ResponseEntity<String> privateEndpoint(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaimAsString("preferred_username");
        logger.info("Service1: Request from user {}", username);
        
        String partnerData = null;
        try {
            logger.info("Service1: Requesting token from Keycloak...");
            
            // Get client_credentials token for Service2
            RestTemplate rest = new RestTemplate();
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "client_credentials");
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            
            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(form, tokenHeaders);
            ResponseEntity<Map> tokenResponse = rest.postForEntity(tokenUri, tokenRequest, Map.class);
            
            String accessToken = (String) tokenResponse.getBody().get("access_token");
            logger.info("Service1: Token obtained successfully");
            
            // Call Service2
            logger.info("Service1: Calling Service2...");
            HttpHeaders headersForService2 = new HttpHeaders();
            headersForService2.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headersForService2);
            
            partnerData = rest.exchange(
                service2Url,
                HttpMethod.GET,
                entity,
                String.class
            ).getBody();
            
            logger.info("Service1: Received from Service2: {}", partnerData);
            
        } catch (Exception e) {
            logger.error("Service1: Error calling Service2", e);
            partnerData = "ERROR: " + e.getMessage();
        }
        
        String message = "Hello " + username + " from Service 1! | Service 2 dit : " + partnerData;
        return ResponseEntity.ok(message);
    }
}