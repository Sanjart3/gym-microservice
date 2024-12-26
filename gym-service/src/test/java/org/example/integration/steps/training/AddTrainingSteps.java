package org.example.integration.steps.training;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.integration.config.TestContext;
import org.example.repositories.TraineeRepository;
import org.example.repositories.TrainerRepository;

import java.util.List;
import java.util.Optional;

import static org.example.integration.util.Constants.AUTHORIZATION_HEADER;
import static org.example.integration.util.Constants.CONTENT_TYPE_JSON;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class AddTrainingSteps {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TestContext testContext;
    private final MockMvc mockMvc;


    private final String TRAINEE_TRAINING_LIST_URL = "/trainee/training";

    private MvcResult mvcResult;


    @Given("the trainer {string} is available in the system")
    public void theTrainerIsAvailableInTheSystem(String trainerUsername) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(trainerUsername);

        assertTrue(trainerOptional.isPresent());
    }

    @Given("the training list are assigned to trainee {string} with trainingId")
    public void theTrainingIsNotAssignedToTraineeWithTrainingId(String traineeUsername) {
        Optional<Trainee> traineeOptional = traineeRepository.findTraineeByUser_Username(traineeUsername);

        assertTrue(traineeOptional.isPresent());
    }

    @When("the trainee requests to add training with: trainerUsername {string}, duration {string}, date {string}, trainingName {string}")
    public void theTraineeRequestsToAddTrainingWithTrainerUsernameDurationDateTrainingName(String trainerUsername, String duration, String date, String trainingName) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("trainerUsername", trainerUsername);
        requestBody.put("duration", duration);
        requestBody.put("date", date);
        requestBody.put("trainingName", trainingName);

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(TRAINEE_TRAINING_LIST_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON)
                        .content(requestBody.toString()))
                .andReturn();
    }

    @And("the training for {string} should be added")
    public void theTrainingForShouldBeAdded(String traineeUsername) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(traineeUsername);

        assertTrue(traineeOptional.isPresent());
        traineeOptional.ifPresent(trainee -> {
            List<Training> trainingList = trainee.getTrainings();

            assertEquals(2, trainingList.size());
        });
    }

    @Then("the response status for training add should be {int}")
    public void theResponseStatusForTrainingAddShouldBe(int status) {
        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @Given("Given the trainer {string} is available in the system")
    public void givenTheTrainerIsAvailableInTheSystem(String trainerUsername) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(trainerUsername);

        assertTrue(trainerOptional.isPresent());
    }
}