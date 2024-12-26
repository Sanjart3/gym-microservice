package org.example.integration.steps.trainer;

import io.cucumber.datatable.DataTable;
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
import org.example.entities.User;
import org.example.integration.config.TestContext;
import org.example.repositories.UserRepository;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.example.integration.util.Constants.AUTHORIZATION_HEADER;
import static org.example.integration.util.Constants.CONTENT_TYPE_JSON;

@RequiredArgsConstructor
public class TrainerSteps {
    private static final String TRAINER_PROFILE_URL = "/trainer/profile";
    private static final String CHANGE_PASSWORD_URL = "/trainer/profile/changePassword";
    private static final String TOGGLE_STATUS_URL = "/trainer/profile/toggleStatus";

    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private MvcResult mvcResult;

    @Given("the trainer is not authenticated with username {string}")
    public void theTrainerIsNotAuthenticated(String username) {
        testContext.setJwtToken(null);
    }

    @When("the trainer requests the trainer profile")
    public void theUserRequestsTheTrainerProfile() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(TRAINER_PROFILE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainer requests to change the password with a valid new password {string}")
    public void theUserRequestsToChangeThePasswordWithAValidNewPassword(String password) throws Exception {
        performPasswordChangeRequest(password);
    }

    @When("the trainer requests to change the password with an invalid new password {string}")
    public void theUserRequestsToChangeThePasswordWithAnInvalidNewPassword(String password) throws Exception {
        performPasswordChangeRequest(password);
    }

    private void performPasswordChangeRequest(String password) throws Exception {
        String passwordJson = new JSONObject().put("password", password).toString();
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(CHANGE_PASSWORD_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .content(passwordJson)
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainer requests to update the profile with complete data:")
    public void theUserRequestsToUpdateTheProfileWithCompleteData(DataTable dataTable) throws Exception {
        performProfileUpdateRequest(dataTable);
    }

    @When("the trainer requests to update the profile with incomplete data:")
    public void theUserRequestsToUpdateTheProfileWithIncompleteData(DataTable dataTable) throws Exception {
        performProfileUpdateRequest(dataTable);
    }

    private void performProfileUpdateRequest(DataTable dataTable) throws Exception {
        JSONObject requestBody = new JSONObject();
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            requestBody.put("firstName", columns.get("firstName"));
            requestBody.put("lastName", columns.get("lastName"));
            requestBody.put("isActive", columns.get("isActive"));
        }
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(TRAINER_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .content(requestBody.toString())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainer requests to toggle the trainer status")
    public void theUserRequestsToToggleTheTrainerStatus() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(TOGGLE_STATUS_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @Then("the response status for trainer should be {int}")
    public void theResponseStatusForTrainerShouldBe(int status) {
        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @And("the response should contain the trainer's profile information:")
    public void theResponseShouldContainTheTrainerSProfileInformation(DataTable dataTable) throws Exception {
        JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString());
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            assertEquals(columns.get("firstName"), response.getString("firstName"));
            assertEquals(columns.get("lastName"), response.getString("lastName"));
            assertEquals(Boolean.parseBoolean(columns.get("userIsActive")), response.getBoolean("userIsActive"));
            assertNotNull(response.getString("specialization"));
            assertNotNull(response.getString("traineeList"));
        }
    }

    @And("the response should contain the updated trainer's profile information:")
    public void theResponseShouldContainTheUpdatedProfileInformation(DataTable dataTable) throws Exception {
        JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString());
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            assertEquals(columns.get("firstName"), response.getString("firstName"));
            assertEquals(columns.get("lastName"), response.getString("lastName"));
            assertEquals(Boolean.parseBoolean(columns.get("userIsActive")), response.getBoolean("userIsActive"));
            assertNotNull(response.getString("specialization"));
            assertNotNull(response.getString("traineeList"));
        }
    }

    @And("status for trainer {string} should be toggled")
    public void statusForTrainerShouldBeToggled(String username) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            assertEquals(false, user.getIsActive());
        } else {
            throw new RuntimeException("User not found");
        }
    }
}