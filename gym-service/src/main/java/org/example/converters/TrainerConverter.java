package org.example.converters;

import org.example.dto.trainer.TrainerDto;
import org.example.dto.trainer.TrainerUpdateRequestDto;
import org.example.entities.Trainer;
import org.example.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TrainerConverter extends AbstractConverter<Trainer, TrainerDto> {

    public TrainerConverter(ModelMapper modelMapper) {
        super(modelMapper);
    }

    public Trainer fromTrainerUpdateRequestToTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto) {
        String firstName = trainerUpdateRequestDto.getFirstName();
        String lastName = trainerUpdateRequestDto.getLastName();
        String username = trainerUpdateRequestDto.getUsername();
        Boolean isActive = trainerUpdateRequestDto.getIsActive();
        User user = new User(firstName, lastName, username, isActive);
        return new Trainer(trainerUpdateRequestDto.getSpecializationId(), user);
    }

    @Override
    protected Class<Trainer> getEntity() {
        return Trainer.class;
    }

    @Override
    protected Class<TrainerDto> getDto() {
        return TrainerDto.class;
    }
}
