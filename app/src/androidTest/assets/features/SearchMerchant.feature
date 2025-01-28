Feature: Paymaart- Customer Android- Search Merchant

  As a customer, I want to be able to search for merchants

  Condition of satisfaction

  1. Users should be able to search for merchants by Paymaart ID/trading name through the Paymaart platform.

  2. The search feature should respond once the entire character is filled


  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    And I enter paymart ID "85957573"
    And I enter login PIN "520736"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    And I click back button on Membership plans page
    Then I am redirected to the homepage

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am redirected to the homepage
    When I click on the Merchant option
    Then I should see a list of merchants I have transacted with in the last 90 days

  Scenario: Searching Merchant by trading name / merchant name
    When I click on the Search tab
    And I enter trading name / Merchant name as "Suhaas"
    Then I should see the Merchant name along with Merchant ID

  Scenario: Clearing the result on Search tab
    When I click on clear button on search tab
    Then the search tab should be empty

  Scenario: Searching merchant by Paymaart ID / Merchant ID
    When I click on the Search tab
    And I enter Paymaart ID / Merchant ID as "5669142"
    Then I should see the Merchant name along with Merchant ID