Feature: Trainee: Add Training

  Scenario: Successfully add training
    Given the trainer "testTrainer" is available in the system
    And the trainee is registered with username: "testTrainee"
    When the trainee requests to add training with: trainerUsername "testTrainer", duration "100", date "2024-12-12", trainingName "testTraining"
    Then the response status for training add should be 200
    And the training for "testTrainee" should be added

  Scenario: Add training with invalid trainerUsername
    Given the training list are assigned to trainee "testTrainee" with trainingId
    And the trainee is registered with username: "testTrainee"
    When the trainee requests to add training with: trainerUsername "notExistingTrainer", duration "100", date "2024-12-12", trainingName "testTraining"
    Then the response status for training add should be 404

  Scenario: Add training with invalid duration
    Given Given the trainer "testTrainer" is available in the system
    And the trainee is registered with username: "testTrainee"
    When the trainee requests to add training with: trainerUsername "testTrainer", duration "0", date "2024-12-12", trainingName "testTraining"
    Then the response status for training add should be 400