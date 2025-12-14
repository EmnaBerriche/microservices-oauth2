package com.example.service2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PartnerController {

    @GetMapping("/partner")
    public ResponseEntity<String> partnerEndpoint(@AuthenticationPrincipal Jwt principal) {

        // Ce token représente le service account de Service 1
        String clientName = principal.getClaimAsString("preferred_username");
        // Avec Keycloak, ce sera souvent : "service-account-<clientId>"

        String body = "Données du partenaire depuis Service 2 pour " + clientName;

        return ResponseEntity.ok(body);
    }
}