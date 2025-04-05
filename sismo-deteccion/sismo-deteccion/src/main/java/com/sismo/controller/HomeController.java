package com.sismo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api/home")
    public Map<String, Boolean> home(Authentication authentication) {
        Map<String, Boolean> rolesMap = new HashMap<>();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
            rolesMap.put("isAdmin", roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")));
            rolesMap.put("isUser", roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_USER")));
        }
        return rolesMap;
    }
}