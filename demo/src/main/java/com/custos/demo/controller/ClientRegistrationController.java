package com.custos.demo.controller;

import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import com.custos.oauth.service.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class ClientRegistrationController {

    @Autowired
    private ClientRegistrationService clientRegistrationService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("request", new RegistrationForm());
        return "register";
    }

    @PostMapping
    public String registerClient(RegistrationForm form, Model model) {
        try {
            ClientRegistrationRequest request = ClientRegistrationRequest.builder()
                .clientName(form.getClientName())
                .clientType("confidential")
                .redirectUris(new String[]{form.getRedirectUri()})
                .grantTypes(new String[]{"authorization_code", "refresh_token"})
                .responseTypes(new String[]{"code"})
                .scopes(form.getScope().split(" "))
                .build();

            ClientRegistrationResponse response = clientRegistrationService.registerClient(request);
            model.addAttribute("clientId", response.getClientId());
            model.addAttribute("clientSecret", response.getClientSecret());
            return "registration-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    public static class RegistrationForm {
        private String clientName;
        private String redirectUri;
        private String scope;

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }
} 