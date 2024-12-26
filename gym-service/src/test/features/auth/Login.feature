Feature: User Authentication
  As a user
  I want to log in to the system
  So that I can access protected resources

  Scenario: Trainer successful login
    Given the trainer is registered with username: "testTrainer"
    When I send a POST request to "/auth/login" with valid credentials: username: "testTrainer", password: "Password123"
    Then the response status should be 200
    And the response body should contain a valid JWT token

  Scenario: Trainee successful login
    Given the trainee is registered with username: "testTrainee"
    When I send a POST request to "/auth/login" with valid credentials: username: "testTrainee", password: "Password123"
    Then the response status should be 200
    And the response body should contain a valid JWT token

  Scenario: Login with invalid credentials
    Given the authentication service is operational
    When I send a POST request to "/auth/login" with invalid credentials
    Then the response status should be 401
    And the response body should contain an error message "Invalid username or password"