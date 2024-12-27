Feature: Trainer: Profile Management

  Scenario: Successfully access profile
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests the trainer profile
    Then the response status for trainer should be 200
    And the response should contain the trainer's profile information:
      | firstName | lastName | specialization | traineeList | userIsActive |

  Scenario: Access profile without JWT
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests the trainer profile
    Then the response status for trainer should be 403

  Scenario: Successfully change password
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests to change the password with a valid new password "Password123"
    Then the response status for trainer should be 200

  Scenario: Change password with invalid data
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests to change the password with an invalid new password "password"
    Then the response status for trainer should be 400

  Scenario:  Successfully update profile
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests to update the profile with complete data:
      | firstName | lastName | isActive |
      | Leo       | Messi    | true     |
    Then the response status for trainer should be 200
    And the response should contain the updated trainer's profile information:
      | firstName | lastName | specialization | traineeList | userIsActive |
      | Leo       | Messi    | CARDIO         |             | true         |

  Scenario: Update profile with incomplete data
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests to update the profile with incomplete data:
      | firstName | lastName | isActive |
      | L         | Mes      | true     |
    Then the response status for trainer should be 400

  Scenario: Successfully toggle status
    Given the trainer is registered with username: "testTrainer"
    When the trainer requests to toggle the trainer status
    Then the response status for trainer should be 200
    And status for trainer "testTrainer" should be toggled