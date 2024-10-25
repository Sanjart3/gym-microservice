package org.example.utils.validation.impl;

import org.example.entities.Training;
import org.example.utils.exception.ValidatorException;
import org.example.utils.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class TrainingValidation implements Validator<Training> {
    @Override
    public Boolean isValidForCreate(Training training) {
        if (training.getTrainingDate()==null){
            throw new ValidatorException("Training date is required!");
        } else if (training.getTrainingName()==null) {
            throw new ValidatorException("Training name is required!");
        } else if (training.getTrainingType()==null) {
            throw new ValidatorException("Training type is required!");
        } else if (training.getTrainingDuration()<0) {
            throw new ValidatorException("Training duration should be positive!");
        } else if (training.getTrainingDuration()>120) {
            throw new ValidatorException("Training duration should be less than 120!");
        }
        return true;
    }

    @Override
    public Boolean isValidForUpdate(Training training) {
        if (training.getId()==null) {
            throw new ValidatorException("Training id is required!");
        } else if (training.getTrainingDate()==null) {
            throw new ValidatorException("Training date is required!");
        } else if (training.getTrainingName()==null) {
            throw new ValidatorException("Training name is required!");
        } else if (training.getTrainingType()==null) {
            throw new ValidatorException("Training type is required!");
        } else if (training.getTrainingDuration()<0) {
            throw new ValidatorException("Training duration should be positive!");
        }  else if (training.getTrainingDuration()>120) {
            throw new ValidatorException("Training duration should be less than 120!");
        }
        return true;
    }
}
