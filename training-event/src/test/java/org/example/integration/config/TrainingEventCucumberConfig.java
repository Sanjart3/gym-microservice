package org.example.integration.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.example.TrainingEventApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainingEventApplication.class)
@ActiveProfiles("test")
@Transactional
public class TrainingEventCucumberConfig {
}