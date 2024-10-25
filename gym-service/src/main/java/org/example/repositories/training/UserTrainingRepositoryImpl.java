package org.example.repositories.training;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.example.dto.CriteriaDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserTrainingRepositoryImpl implements UserTrainingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Training> searchTraineeTraining(CriteriaDto criteriaDto, String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> criteriaQuery = criteriaBuilder.createQuery(Training.class);
        Root<Training> trainingRoot = criteriaQuery.from(Training.class);

        //joins
        Join<Training, Trainee> traineeJoin = trainingRoot.join("trainee");
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(traineeUserJoin.get("username"), username));

        if (criteriaDto.getFromDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(trainingRoot.get("trainingDate"), criteriaDto.getFromDate()));
        }
        if (criteriaDto.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(trainingRoot.get("trainingDate"), criteriaDto.getToDate()));
        }
        if (criteriaDto.getTrainingType() != null) {
            predicates.add(criteriaBuilder.equal(trainingRoot.get("trainingType"), criteriaDto.getTrainingType()));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Training> searchTrainerTraining(CriteriaDto criteriaDto, String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> criteriaQuery = criteriaBuilder.createQuery(Training.class);
        Root<Training> trainingRoot = criteriaQuery.from(Training.class);

        //joins
        Join<Training, Trainer> trainerJoin = trainingRoot.join("trainer");
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(trainerUserJoin.get("username"), username));

        if (criteriaDto.getFromDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(trainingRoot.get("trainingDate"), criteriaDto.getFromDate()));
        }
        if (criteriaDto.getToDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(trainingRoot.get("trainingDate"), criteriaDto.getToDate()));
        }
        if (criteriaDto.getTrainingType() != null) {
            predicates.add(criteriaBuilder.equal(trainingRoot.get("trainingType"), criteriaDto.getTrainingType()));
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
