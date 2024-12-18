package org.example.repositories;


import org.example.entities.TrainerSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingEventRepository extends MongoRepository<TrainerSummary, Long> {

    TrainerSummary findByUsernameEquals(String username);

    List<TrainerSummary> getAllBy();
}
