package org.example;

import org.example.externaldto.TrainingEventDto;
import org.example.entities.TrainerSummary;
import org.example.enums.ActionType;
import org.example.repositories.TrainingEventRepository;
import org.example.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TrainingEventApplicationTests {

    @Mock
    private TrainingEventRepository trainingEventRepository;

    @Mock
    private ModelMapper modelMapper;
    @Autowired
    private TrainingService trainingService;

    @Test
    void testCreate() {
        TrainingEventDto trainingEventDTO = new TrainingEventDto();
        trainingEventDTO.setTrainerUsername("johndoe");
        trainingEventDTO.setTrainerFirstName("John");
        trainingEventDTO.setTrainerLastName("Doe");
        trainingEventDTO.setActive(true);
        trainingEventDTO.setTrainingDate(new Date(String.valueOf(LocalDate.now())));
        trainingEventDTO.setTrainingDuration(2);
        trainingEventDTO.setActionType(ActionType.ADD.toString());

        TrainerSummary trainingEvent = new TrainerSummary();
        trainingEvent.setId(1L);
        trainingEvent.setUsername("johndoe");
        trainingEvent.setFirstName("John");
        trainingEvent.setLastName("Doe");
        trainingEvent.setIsActive(true);
        trainingEvent.setTrainingDate(LocalDate.now());
        trainingEvent.setTrainingDuration(2);
        trainingEvent.setActionType(ActionType.ADD);

        when(modelMapper.map(trainingEventDTO, TrainerSummary.class)).thenReturn(trainingEvent);
        when(trainingEventRepository.save(trainingEvent)).thenReturn(trainingEvent);

        trainingService.saveTrainingEvent(trainingEventDTO);

        verify(modelMapper, times(1)).map(trainingEventDTO, TrainerSummary.class);
        verify(trainingEventRepository, times(1)).save(trainingEvent);
    }
}
