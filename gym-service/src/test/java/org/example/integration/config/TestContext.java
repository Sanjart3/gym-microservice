package org.example.integration.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TestContext {
    private String jwtToken;
}