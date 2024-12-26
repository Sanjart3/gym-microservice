package org.example.integration.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.GymBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = GymBootApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GymAppCucumberConfiguration {
}
