package org.example.repositories;


import org.example.entities.TrainingEvents;
import org.example.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvents, Long> {
    List<TrainingEvents> findAllByActionType(ActionType action);
}
