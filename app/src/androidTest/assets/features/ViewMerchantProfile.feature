Feature: Paymaart- Customer Android- View Merchant profile

  As a Customer , I want an option to view the merchant profile

  Condition of satisfaction

  1. There should an option to view merchant profile basic details

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "25465668"
    And I enter login PIN "970541"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am redirected to the homepage
    When I click on the Merchant option
    Then I should see a list of merchants I have transacted with in the last 90 days

  Scenario: Searching Merchant by trading name
    When I click on the Search tab
    And I enter trading name as "qwerty"
    Then I should read a message stating "No Data Found"
    When I click on clear button on search tab
    Then the search tab should be empty
    When I click on the Search tab
    And I enter trading name as "Suhaas"
    Then I should see the Trading name along with Paymaart ID
    Then I select the Merchant "Suhaas Kumar TEST"

  Scenario: Viewing Merchant Profile
    Given I am on the chat screen
    When I click on the Merchant name
    Then I should be able to see the Merchant profile with details