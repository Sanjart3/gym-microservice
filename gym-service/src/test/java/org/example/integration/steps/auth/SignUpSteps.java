package org.example.integration.steps.auth;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
@Transactional
public class SignUpSteps {
    private final MockMvc mockMvc;

    private MvcResult mvcResult;
    private String requestBody;

    @Given("the user service is running")
    public void theUserServiceIsRunning() {
        assertNotNull(mockMvc);
    }

    @When("Trainee signUp with firstName {string}, lastname {string}, dateBirth {string} and address {string}")
    public void traineeSignUpWithFirstNameLastnameDateBirthAndAddress(String fistName, String lastName, String dateBirth, String address) throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", fistName);
        request.put("lastName", lastName);
        request.put("dateOfBirth", dateBirth);
        request.put("address", address);

        requestBody = request.toString();
        this.mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/trainee/sign-up")
                        .content(requestBody)
                        .contentType("application/json"))
                .andReturn();
    }

    @When("Trainer signUp with firstName {string}, lastname {string} and specialization {string}")
    public void trainerSignUpWithFirstNameLastnameAndSpecialization(String firstName, String lastName, String specialization) throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", firstName);
        request.put("lastName", lastName);
        request.put("specialization", specialization);

        requestBody = request.toString();
        this.mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/trainer/sign-up")
                        .content(requestBody)
                        .contentType("application/json"))
                .andReturn();
    }


    @Then("the signup response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @And("the response should contain new username and password")
    public void theResponseShouldContainNewUsernameAndPassword() throws Exception {
        JSONObject response = parseResponse(mvcResult);
        assertEquals(2, response.length());
        assertNotNull(response.get("username"));
        assertNotNull(response.get("password"));
    }

    private JSONObject parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return new JSONObject(content);
    }


}