package org.example.services.impl;

import org.example.config.CustomUserDetails;
import org.example.dto.AuthDto;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.example.services.JWTService;
import org.example.services.UserDetailsService;
import org.example.exception.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public CustomUserDetailsService(@Lazy UserRepository userRepository, @Lazy AuthenticationManager authenticationManager, @Lazy JWTService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new NotFoundException("User", "username");
        }
        User user = optional.get();
        return new CustomUserDetails(user);
    }

    @Override
    public String login(AuthDto authDto) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authDto.getUsername(),
                    authDto.getPassword()
            ));
        } catch (AuthenticationException e) {
            throw new org.example.exception.AuthenticationException("Invalid username or password");
        }
        UserDetails userDetails = loadUserByUsername(authDto.getUsername());
        return jwtService.generateToken(userDetails);
    }
}
