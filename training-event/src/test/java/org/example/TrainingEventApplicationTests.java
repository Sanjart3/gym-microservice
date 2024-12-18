package org.example;

import org.example.externaldto.TrainingEventDto;
import org.example.entities.TrainerSummary;
import org.example.enums.ActionType;
import org.example.repositories.TrainingEventRepository;
import org.example.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TrainingEventApplicationTests {

    @Mock
    private TrainingEventRepository trainingEventRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TrainingService trainingService;

    TrainerSummary trainerSummary;
    TrainingEventDto trainingEventDto;

    @BeforeEach
    void testCreate() {
        trainingEventDto = new TrainingEventDto();
        trainingEventDto.setTrainerUsername("johndoe");
        trainingEventDto.setTrainerFirstName("John");
        trainingEventDto.setTrainerLastName("Doe");
        trainingEventDto.setActive(true);
        trainingEventDto.setTrainingDate(LocalDate.now());
        trainingEventDto.setTrainingDuration(2);
        trainingEventDto.setActionType(ActionType.ADD.toString());

        trainerSummary = new TrainerSummary();
        trainerSummary.setUsername("johndoe");
    }

    @Test
    void create_whenTrainerSummaryExists_addsDurationAndSaves() {
        when(trainingEventRepository.findByUsernameEquals("trainer123")).thenReturn(trainerSummary);
        when(trainingEventRepository.save(trainerSummary)).thenReturn(trainerSummary);

        trainingService.saveTrainingEvent(trainingEventDto);

        verify(trainingEventRepository).save(any(TrainerSummary.class));
        verify(modelMapper, never()).map(trainingEventDto, any(TrainerSummary.class));
    }

    @Test
    void create_whenTrainerSummaryNotExists_convertsAndSaves() {
        when(trainingEventRepository.findByUsernameEquals("trainer123")).thenReturn(null);
        when(modelMapper.map(trainerSummary, TrainerSummary.class)).thenReturn(trainerSummary);
        when(trainingEventRepository.save(trainerSummary)).thenReturn(trainerSummary);

        trainingService.saveTrainingEvent(trainingEventDto);

        verify(trainingEventRepository).save(trainerSummary);
        verify(modelMapper).map(trainingEventDto, TrainingEventDto.class);
    }
}
