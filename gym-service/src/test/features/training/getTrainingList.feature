Feature: Get Training List

  Scenario:Trainee: Successfully retrieve all training
    Given the training list are assigned to trainee "testTrainee"
    And the trainer is registered with username: "testTrainee"
    When the trainee requests the list of training list without criteria
    Then the response should contain a list of training
    And the response status for training list should be 200

  Scenario:Trainee: Successfully retrieve all training by criteria
    Given the training list are assigned to trainee "testTrainee"
    And the trainer is registered with username: "testTrainer"
    When the trainee requests the list of training list with criteria: periodFrom "2000-01-01", periodTo "2028-12-31", trainerUsername "testTrainer"
    Then the response should contain a list of training
    And the response status for training list should be 200

  Scenario: Retrieve trainings with invalid criteria
    Given the training list are assigned to trainee "testTrainee"
    And the trainer is registered with username: "testTrainer"
    When the trainee requests the list of training list with criteria: periodFrom "2028-12-", periodTo "2000-01-01", trainerUsername "testTrainer"
    Then the response status for training list should be 400

  Scenario:Trainer: Successfully retrieve all training
    Given the training list are assigned to trainer "testTrainer"
    And the trainer is registered with username: "testTrainer"
    When the trainer requests the list of training list without criteria
    Then the response should contain a list of training
    And the response status for training list should be 200

  Scenario:Trainer: Successfully retrieve all training for trainer by criteria
    Given the training list are assigned to trainer "testTrainer"
    And the trainer is registered with username: "testTrainer"
    When the trainer requests the list of training list with criteria: periodFrom "2000-01-01", periodTo "2028-12-31", traineeUsername "testTrainee"
    Then the response should contain a list of training
    And the response status for training list should be 200