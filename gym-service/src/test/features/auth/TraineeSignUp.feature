Feature: Trainee: SignUp

  Scenario: SignUp with correct data
    Given the user service is running
    When Trainee signUp with firstName "Leo", lastname "Messi", dateBirth "2003-04-06" and address "1234"
    Then the signup response status should be 201
    And the response should contain new username and password

  Scenario: SignUp with incorrect name
    Given the user service is running
    When Trainee signUp with firstName "L", lastname "Messi", dateBirth "2003-04-06" and address "1234"
    Then the signup response status should be 400

  Scenario: SignUp with incorrect dateBirth
    Given the user service is running
    When Trainee signUp with firstName "Leo", lastname "Messi", dateBirth "2003-0406" and address "1234"
    Then the signup response status should be 400