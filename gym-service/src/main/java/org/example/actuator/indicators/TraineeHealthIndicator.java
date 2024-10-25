package org.example.actuator.indicators;

import org.example.repositories.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TraineeHealthIndicator implements HealthIndicator {

    @Autowired
    private TraineeRepository traineeRepository;

    @Override
    public Health health() {
        try {
            int traineeCount = traineeRepository.findAll().size();
            if (traineeCount > 0) {
                return Health.up().withDetail("Trainee count", traineeCount).build();
            } else {
                return Health.down().withDetail("Trainee count", "Not trainers found").build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
