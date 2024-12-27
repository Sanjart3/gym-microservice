Feature: Trainer: signUp

  Scenario: SignUp with correct data
    Given the user service is running
    When Trainer signUp with firstName "Leo", lastname "Messi" and specialization "BENCH_PRESS"
    Then the signup response status should be 201
    And the response should contain new username and password

  Scenario: SignUp with incorrect specialization
    Given the user service is running
    When Trainer signUp with firstName "Leo", lastname "Messi" and specialization "BENCH"
    Then the signup response status should be 400

  Scenario: SignUp with incorrect name
    Given the user service is running
    When Trainer signUp with firstName "L", lastname "Mes" and specialization "BENCH_PRESS"
    Then the response status should be 400