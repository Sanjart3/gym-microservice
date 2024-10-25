package org.example.converters;

import org.example.dto.training.TrainingCreateRequest;
import org.example.dto.training.TrainingDto;
import org.example.entities.Training;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TrainingConverter extends AbstractConverter<Training, TrainingDto> {

    public TrainingConverter(ModelMapper modelMapper) {
        super(modelMapper);
    }

    public Training fromTrainingCreateRequestToTraining(TrainingCreateRequest trainingCreateRequest) {
        Training training = new Training();
        training.setTrainingName(trainingCreateRequest.getTrainingName());
        training.setTrainingDate(trainingCreateRequest.getTrainingDate());
        training.setTrainingDuration(trainingCreateRequest.getTrainingDuration());
        return training;
    }

    @Override
    protected Class<Training> getEntity() {
        return Training.class;
    }

    @Override
    protected Class<TrainingDto> getDto() {
        return TrainingDto.class;
    }
}
