package org.example.integration.steps.auth;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.config.CustomUserDetails;
import org.example.entities.User;
import org.example.integration.config.TestContext;
import org.example.repositories.UserRepository;
import org.json.JSONObject;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.example.services.JWTService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
@Transactional
public class LoginStep {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final TestContext testContext;

    private final MockMvc mockMvc;

    private MvcResult mvcResult;

    @Given("the trainer is registered with username: {string}")
    public void theTrainerIsAuthenticated(String username) {
        setToken(username);
    }

    @Given("the trainee is registered with username: {string}")
    public void theTraineeIsAuthenticated(String username) {
        setToken(username);
    }

    @When("I send a POST request to {string} with valid credentials: username: {string}, password: {string}")
    public void UserLoginWithUsernameAndPassword(String url, String username, String password) throws Exception {
        JSONObject request = new JSONObject();
        request.put("username", username);
        request.put("password", password);

        this.mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(request.toString())
                        .contentType("application/json"))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Then("the response status should be {int}")
    public void theResponseStatusForLoginShouldBe(int status) {
        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @And("the response body should contain a valid JWT token")
    public void userShouldReceiveAToken() throws Exception {
        String token = mvcResult.getResponse().getContentAsString();
        assertNotNull(jwtService.extractUserName(token));
    }


    private void setToken(String username) {
        User user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String token = jwtService.generateToken(new CustomUserDetails(user));
        testContext.setJwtToken(token);
    }

}