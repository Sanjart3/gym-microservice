package org.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.example.entities.TrainingType;
import org.example.enums.TrainingsType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends CrudRepository<TrainingType, Integer> {
    Optional<TrainingType> getTrainingTypeByName(String name);

    List<TrainingType> getAllBy();
}