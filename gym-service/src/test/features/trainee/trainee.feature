Feature: Trainee: Profile Management

  Scenario: Successfully access profile
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests the trainee profile
    Then the response status for trainee should be 200
    And the response should contain the trainee's profile information:
      | firstName | lastName | dateOfBirth | trainerList | userIsActive | address |

  Scenario: Access profile without JWT
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests the trainee profile
    Then the response status for trainee should be 403

  Scenario: Successfully change password
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to change the password with a valid new password "Password123"
    Then the response status for trainee should be 200

  Scenario: Change password with invalid data
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to change the password with an invalid new password "password"
    Then the response status for trainee should be 400

  Scenario: Successfully update profile
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to update the profile with complete data:
      | firstName | lastName | isActive | dateOfBirth | address   |
      | Leo       | Messi    | true     | 2003-25-12  | 23 Avenue |
    Then the response status for trainee should be 200
    And the response should contain the updated trainee's profile information:
      | firstName | lastName | dateOfBirth | trainerList | userIsActive | address |
      | Leo       | Messi    | 2003-12-25  |             | true         | 123 HT  |

  Scenario: Update profile with incomplete data
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to update the profile with incomplete data:
      | firstName | lastName | isActive |
      | L         | Mes      | true     |
    Then the response status for trainee should be 400

  Scenario: Successfully toggle status
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to toggle the trainee status
    Then the response status for trainee should be 200
    And status for trainee "testTrainee" should be toggled

  Scenario: Successfully delete trainee
    Given the trainee is registered with username: "testTrainee"
    When the trainee requests to delete the trainee "testTrainee"
    Then the response status for trainee should be 200
    And the trainee "testTrainee" should be deleted