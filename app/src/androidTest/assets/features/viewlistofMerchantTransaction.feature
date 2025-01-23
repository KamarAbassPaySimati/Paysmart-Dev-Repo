Feature: Paymaart- Customer Android- List Merchant Transaction

  As a customer, I want to be able to view list of merchants transaction

  Condition of satisfaction
  1.Upon results shown on a searched merchant by user, there should be a option to see list of last 90 days transactions

  Background: Logging into admin approved customer account
    Given the login screen is displayed
    When I choose to log in with my Paymaart ID
    And I enter Paymaart ID "18505920"
    And I enter the login PIN "852369"
    And I click on the login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to the home screen

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am on the Home screen
    When I click on the "Merchant" option
    Then I should see a list of merchants I have transacted with in the last 90 days




