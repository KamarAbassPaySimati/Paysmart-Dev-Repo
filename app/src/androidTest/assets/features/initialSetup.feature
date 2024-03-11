Feature: Analyze the Initial screen
  The Initial screen should display different names according to respective environments

  Scenario: Show welcome message
    Given I have the Initial screen
    Then I should see respective welcome message