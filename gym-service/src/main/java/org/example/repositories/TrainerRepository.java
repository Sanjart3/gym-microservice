package org.example.repositories;

import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.repositories.training.UserTrainingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long>, UserTrainingRepository {

    Optional<Trainer> findByUser_Username(String username);

    Long findIdByUser_Username(String username);

    Optional<Trainer> findByUser_UsernameAndUser_Password(String username, String password);

    Long countByUser_UsernameStartsWith(String username);
}
