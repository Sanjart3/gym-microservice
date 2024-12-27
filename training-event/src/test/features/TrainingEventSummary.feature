Feature: Retrieve Training Event Summaries

  Scenario: Successfully retrieve trainer summaries
    Given the training service is operational
    When I send a GET request to "/event/summary"
    Then the response status should be 200
    And the response body should contain a list of trainer summaries

  Scenario: Fail to retrieve trainer summaries when service is unavailable
    Given the training service is unavailable
    When I send a GET request for unavailable service to "/event/summary"
    Then the response status should be 500
    And the response body should contain an error message