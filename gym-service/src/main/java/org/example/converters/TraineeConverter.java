package org.example.converters;

import org.example.dto.trainee.TraineeDto;
import org.example.dto.trainee.TraineeUpdateRequestDto;
import org.example.entities.Trainee;
import org.example.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TraineeConverter extends AbstractConverter<Trainee, TraineeDto> {

    public TraineeConverter(ModelMapper modelMapper) {
        super(modelMapper);
    }

    public Trainee updateRequestDtoToTrainee(TraineeUpdateRequestDto traineeUpdateRequestDto) {
        String firstName = traineeUpdateRequestDto.getFirstName();
        String lastName = traineeUpdateRequestDto.getLastName();
        String username = traineeUpdateRequestDto.getUsername();
        Boolean isActive = traineeUpdateRequestDto.getIsActive();
        User user = new User(firstName, lastName, username, isActive);
        LocalDate dob = traineeUpdateRequestDto.getDob();
        String address = traineeUpdateRequestDto.getAddress();
        return new Trainee(dob, address, user);
    }

    @Override
    protected Class<Trainee> getEntity() {
        return Trainee.class;
    }

    @Override
    protected Class<TraineeDto> getDto() {
        return TraineeDto.class;
    }
}
