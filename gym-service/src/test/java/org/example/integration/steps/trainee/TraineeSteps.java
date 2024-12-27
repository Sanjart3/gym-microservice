package org.example.integration.steps.trainee;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.example.integration.util.Constants.AUTHORIZATION_HEADER;
import static org.example.integration.util.Constants.CONTENT_TYPE_JSON;

@RequiredArgsConstructor
public class TraineeSteps {
    private static final String TRAINEE_PROFILE_URL = "/trainee/profile";
    private static final String CHANGE_PASSWORD_URL = "/trainee/profile/changePassword";
    private static final String TOGGLE_STATUS_URL = "/trainee/profile/toggleStatus";


    private final TestContext testContext;
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private MvcResult mvcResult;

    @Given("the trainee is not authenticated with username {string}")
    public void theTraineeIsNotAuthenticated(String username) {
        testContext.setJwtToken(null);
    }

    @When("the trainee requests the trainee profile")
    public void theTraineeRequestsTheTraineeProfile() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(TRAINEE_PROFILE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainee requests to change the password with a valid new password {string}")
    public void theTraineeRequestsToChangeThePasswordWithAValidNewPassword(String password) throws Exception {
        performPasswordChangeRequest(password);
    }

    @When("the trainee requests to change the password with an invalid new password {string}")
    public void theTraineeRequestsToChangeThePasswordWithAnInvalidNewPassword(String password) throws Exception {
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

    @When("the trainee requests to update the profile with complete data:")
    public void theTraineeRequestsToUpdateTheProfileWithCompleteData(DataTable dataTable) throws Exception {
        performProfileUpdateRequest(dataTable);
    }

    @When("the trainee requests to update the profile with incomplete data:")
    public void theTraineeRequestsToUpdateTheProfileWithIncompleteData(DataTable dataTable) throws Exception {
        performProfileUpdateRequest(dataTable);
    }

    private void performProfileUpdateRequest(DataTable dataTable) throws Exception {
        JSONObject requestBody = new JSONObject();
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            requestBody.put("firstName", columns.get("firstName"));
            requestBody.put("lastName", columns.get("lastName"));
            requestBody.put("isActive", columns.get("isActive"));
            requestBody.put("dateOfBirth", columns.get("dateOfBirth"));
            requestBody.put("address", columns.get("address"));
        }
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(TRAINEE_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .content(requestBody.toString())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainee requests to toggle the trainee status")
    public void theTraineeRequestsToToggleTheTraineeStatus() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(TOGGLE_STATUS_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @When("the trainee requests to delete the trainee {string}")
    public void theTraineeRequestsToDeleteTheTrainee(String username) throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(TRAINEE_PROFILE_URL)
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER + testContext.getJwtToken())
                        .contentType(CONTENT_TYPE_JSON))
                .andReturn();
    }

    @Then("the response status for trainee should be {int}")
    public void theResponseStatusForTraineeShouldBe(int status) {
        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @And("the response should contain the trainee's profile information:")
    public void theResponseShouldContainTheTraineeSProfileInformation(DataTable dataTable) throws Exception {
        JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString());
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            assertEquals(columns.get("firstName"), response.getString("firstName"));
            assertEquals(columns.get("lastName"), response.getString("lastName"));
            assertEquals(columns.get("dateOfBirth"), response.getString("dateOfBirth"));
            assertEquals(columns.get("address"), response.getString("address"));
            assertEquals(Boolean.parseBoolean(columns.get("userIsActive")), response.getBoolean("userIsActive"));
            assertNotNull(response.getString("trainerList"));
        }
    }

    @And("the response should contain the updated trainee's profile information:")
    public void theResponseShouldContainTheUpdatedTraineeSProfileInformation(DataTable dataTable) throws Exception {
        JSONObject response = new JSONObject(mvcResult.getResponse().getContentAsString());
        for (Map<String, String> columns : dataTable.asMaps(String.class, String.class)) {
            assertEquals(columns.get("firstName"), response.getString("firstName"));
            assertEquals(columns.get("lastName"), response.getString("lastName"));
            assertNotNull(response.getString("dateOfBirth"));
            assertNotNull(response.getString("address"));
            assertEquals(Boolean.parseBoolean(columns.get("userIsActive")), response.getBoolean("userIsActive"));
            assertNotNull(response.getString("trainerList"));
        }
    }

    @And("status for trainee {string} should be toggled")
    public void statusForTraineeShouldBeToggled(String username) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            assertEquals(false, user.getIsActive());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @And("the trainee {string} should be deleted")
    public void theTraineeShouldBeDeleted(String username) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        assertFalse(userOptional.isPresent());
    }
}
