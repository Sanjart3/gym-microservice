package org.example.trainingevent;

import org.example.trainingevent.dto.TrainingEventsDto;
import org.example.trainingevent.entities.TrainingEvents;
import org.example.trainingevent.enums.ActionType;
import org.example.trainingevent.repositories.TrainingEventRepository;
import org.example.trainingevent.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

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
        TrainingEventsDto trainingEventDTO = new TrainingEventsDto();
        trainingEventDTO.setTrainerUsername("johndoe");
        trainingEventDTO.setTrainerFirstName("John");
        trainingEventDTO.setTrainerLastName("Doe");
        trainingEventDTO.setIsActive(true);
        trainingEventDTO.setTrainingDate(LocalDate.now());
        trainingEventDTO.setTrainingDuration(2);
        trainingEventDTO.setActionType(ActionType.ADD.toString());

        TrainingEvents trainingEvent = new TrainingEvents();
        trainingEvent.setId(1L);
        trainingEvent.setTrainerUsername("johndoe");
        trainingEvent.setTrainerFirstName("John");
        trainingEvent.setTrainerLastName("Doe");
        trainingEvent.setIsActive(true);
        trainingEvent.setTrainingDate(LocalDate.now());
        trainingEvent.setTrainingDuration(2);
        trainingEvent.setActionType(ActionType.ADD);

        when(modelMapper.map(trainingEventDTO, TrainingEvents.class)).thenReturn(trainingEvent);
        when(trainingEventRepository.save(trainingEvent)).thenReturn(trainingEvent);

        trainingService.saveTrainingEvent(trainingEventDTO);

        verify(modelMapper, times(1)).map(trainingEventDTO, TrainingEvents.class);
        verify(trainingEventRepository, times(1)).save(trainingEvent);
    }
}
