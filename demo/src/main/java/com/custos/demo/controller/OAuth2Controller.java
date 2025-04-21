package com.custos.demo.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class OAuth2Controller {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2Controller(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/oauth2/authorize")
    public String authorize(@RequestParam Map<String, String> parameters) {
        // Redirect to the OAuth server's authorization endpoint
        return "redirect:http://localhost:9000/oauth2/authorize?" + 
            "response_type=" + parameters.get("response_type") + 
            "&client_id=" + parameters.get("client_id") + 
            "&redirect_uri=" + parameters.get("redirect_uri") + 
            "&scope=" + parameters.get("scope");
    }
} 