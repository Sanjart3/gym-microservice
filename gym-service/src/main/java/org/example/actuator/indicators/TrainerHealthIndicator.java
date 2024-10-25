package org.example.actuator.indicators;

import org.example.repositories.TrainerRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainerHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    public TrainerHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        try {
            long count = trainerRepository.count();
            if (count > 0) {
                return Health.up().withDetail("Trainer Count", count).build();
            } else {
                return Health.down().withDetail("Trainer Count", "No trainers found").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("Error", "Unable to access trainers").build();
        }
    }
}
