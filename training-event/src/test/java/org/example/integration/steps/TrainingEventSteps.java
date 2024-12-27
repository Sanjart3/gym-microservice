package org.example.integration.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.example.dto.TrainerSummaryDto;
import org.example.enums.ActionType;
import org.example.externaldto.TrainingEventDto;
import org.example.services.TrainingService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TrainingEventSteps {
    private final WebApplicationContext webApplicationContext;
    private final ObjectMapper objectMapper;
    @MockBean
    private final TrainingService trainingEventService = Mockito.mock(TrainingService.class);

    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @Given("the training service is operational")
    public void trainingServiceIsOperational() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        TrainingEventDto trainingEventDTO = new TrainingEventDto();
        trainingEventDTO.setTrainerUsername("test");
        trainingEventDTO.setTrainerFirstName("Test");
        trainingEventDTO.setTrainerLastName("User");
        trainingEventDTO.setActive(true);
        trainingEventDTO.setTrainingDate(LocalDate.now());
        trainingEventDTO.setTrainingDuration(200);
        trainingEventDTO.setActionType(String.valueOf(ActionType.ADD));

        trainingEventService.saveTrainingEvent(trainingEventDTO);
    }

    @Given("the training service is unavailable")
    public void trainingServiceIsUnavailable() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        Mockito.doThrow(new RuntimeException("Service unavailable")).when(trainingEventService).countTrainerSummary();
    }


    @When("I send a GET request to {string}")
    public void sendGetRequest(String url) throws Exception {
        this.mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();
    }

    @When("I send a GET request for unavailable service to {string}")
    public void sendUnavailableGetRequest(String url) throws Exception {
        TrainingEventDto trainingEventDTO = new TrainingEventDto();
        trainingEventDTO.setTrainerUsername("test");
        trainingEventDTO.setTrainerFirstName("Test");
        trainingEventDTO.setTrainerLastName("User");
        trainingEventDTO.setActive(true);

        String requestBody = objectMapper.writeValueAsString(trainingEventDTO);

        this.mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatus(int statusCode) throws Exception {
        List<TrainerSummaryDto> trainerSummaries = parseResponse(mvcResult);
        Assertions.assertNotNull(trainerSummaries, "Trainer summaries list should not be null");
        Assertions.assertFalse(trainerSummaries.isEmpty(), "Trainer summaries list should not be empty");
    }

    @Then("the response body should contain a list of trainer summaries")
    public void verifyResponseBodyContainsSummaries() throws Exception {
        List<TrainerSummaryDto> trainerSummaries = parseResponse(mvcResult);
        Assertions.assertNotNull(trainerSummaries, "Trainer summaries list should not be null");
        Assertions.assertFalse(trainerSummaries.isEmpty(), "Trainer summaries list should not be empty");
    }

    @Then("the response body should contain an error message")
    public void verifyResponseBodyContainsErrorMessage() throws Exception {
        String responseContent = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseContent, "Response body should not be null");
        Assertions.assertTrue(responseContent.contains("Service unavailable"),
                "Response body should contain an error message");
    }

    private List<TrainerSummaryDto> parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>() {
        });
    }
}