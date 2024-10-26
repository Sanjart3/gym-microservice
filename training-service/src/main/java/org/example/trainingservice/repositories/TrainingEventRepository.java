package org.example.trainingservice.repositories;

import org.example.trainingservice.entities.TrainingEvents;
import org.example.trainingservice.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvents, Long> {
    List<TrainingEvents> findAllByAction(ActionType action);
}
