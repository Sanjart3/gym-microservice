package org.example.converters;

import org.example.dto.training.TrainingCreateRequest;
import org.example.dto.training.TrainingDto;
import org.example.externaldto.TrainingEventDto;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TrainingConverter extends AbstractConverter<Training, TrainingDto> {

    public TrainingConverter(ModelMapper modelMapper) {
        super(modelMapper);
    }

    public Training toTraining(TrainingDto trainingDto) {
        return modelMapper.map(trainingDto, Training.class);
    }

    public Training fromTrainingCreateRequestToTraining(TrainingCreateRequest trainingCreateRequest) {
        Training training = new Training();
        training.setTrainingName(trainingCreateRequest.getTrainingName());
        training.setTrainingDate(trainingCreateRequest.getTrainingDate());
        training.setTrainingDuration(trainingCreateRequest.getTrainingDuration());
        return training;
    }

    public TrainingEventDto fromTrainingToTrainingEventDto(Training training) {
        TrainingEventDto trainingEvent = new TrainingEventDto();
        Trainer trainer = training.getTrainer();
        trainingEvent.setTrainerUsername(trainer.getUser().getUsername());
        trainingEvent.setTrainerFirstName(trainer.getUser().getFirstName());
        trainingEvent.setTrainerLastName(trainer.getUser().getLastName());
        trainingEvent.setTrainingDate(training.getTrainingDate());
        trainingEvent.setTrainingDuration(training.getTrainingDuration());
        trainingEvent.setActive(trainer.getUser().getIsActive());
        return trainingEvent;
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
