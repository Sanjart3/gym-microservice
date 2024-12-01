package org.example.repositories;

import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.repositories.training.UserTrainingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long>, UserTrainingRepository {

    @Query("SELECT t FROM Trainee t JOIN User u ON t.user=u WHERE u.username=:username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    @Query("SELECT tr FROM Trainer tr WHERE tr NOT IN " +
            "(SELECT tr FROM Trainee t JOIN Trainer tr JOIN User u WHERE u.username = :username)")
    List<Trainer> findUnAssignedTrainersByUsername(@Param("username") String username);

    Optional<Trainee> findTraineeByUser_UsernameAndUser_Password(String username, String password);

    Boolean deleteByUser_Username(String username);

    Long countByUser_UsernameStartsWith(String username);
}
