package com.custos.oauth.authenticators;

import com.custos.oauth.exception.ResourceNotFoundException;
import com.custos.oauth.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordAuthenticator implements Authenticator {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PasswordAuthenticator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String authenticate(String username, String password) {
        return userService.findByUsername(username)
            .map(user -> {
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return user.getId().toString();
                }
                throw new ResourceNotFoundException("Invalid password");
            })
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
} 