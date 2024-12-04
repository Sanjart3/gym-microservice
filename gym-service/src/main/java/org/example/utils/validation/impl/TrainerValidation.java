package org.example.utils.validation.impl;

import org.example.entities.Trainer;
import org.example.entities.User;
import org.example.exception.ValidatorException;
import org.example.utils.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class TrainerValidation implements Validator<Trainer> {
    @Override
    public Boolean isValidForCreate(Trainer trainer) {
        User user = trainer.getUser();
        if (user.getFirstName()==null){
            throw new ValidatorException("First name is required");
        } else if (user.getLastName()==null){
            throw new ValidatorException("Last name is required");
        }
        return true;
    }

    @Override
    public Boolean isValidForUpdate(Trainer trainer) {
        User user = trainer.getUser();
        if (user.getFirstName()==null){
            throw new ValidatorException("First name is required");
        } else if (user.getLastName()==null){
            throw new ValidatorException("Last name is required");
        } else if (user.getUsername()==null){
            throw new ValidatorException("Username is required");
        } else if (user.getIsActive()==null){
            throw new ValidatorException("Is active is required");
        }
        return true;
    }
}
