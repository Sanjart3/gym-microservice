package org.example.trainingservice.repositories;

import org.example.trainingservice.entities.TrainingEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvents, Long> {
}
