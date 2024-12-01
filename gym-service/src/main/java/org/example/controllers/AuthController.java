package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthDto;
import org.example.utils.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.example.services.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController()
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthDto authDto) throws Exception {
        try {
            log.info("Authenticating user: {}", authDto.getUsername());
            userDetailsService.login(authDto);
        } catch (AuthenticationException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final String jwt = userDetailsService.login(authDto);

        return ResponseEntity.ok(jwt);
    }


}
