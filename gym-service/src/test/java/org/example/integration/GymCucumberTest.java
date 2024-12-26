package org.example.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/features"},
        plugin = {"pretty"},
        glue = {"org.example.integration.steps", "org.example.integration.config"})
public class GymCucumberTest {
}
