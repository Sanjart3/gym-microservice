package org.example.trainingevent.repositories;


import org.example.trainingevent.entities.TrainingEvents;
import org.example.trainingevent.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvents, Long> {
    List<TrainingEvents> findAllByActionType(ActionType action);
}
