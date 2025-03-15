Feature: Paymaart- Customer Android- List Merchant Transaction

  As a customer, I want to be able to view list of merchants transaction

  Condition of satisfaction
  1.Upon results shown on a searched merchant by user, there should be a option to see list of last 90 days transactions

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




