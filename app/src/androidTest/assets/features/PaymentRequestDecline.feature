Feature: Paymaart- Customer Android- Payment request decline

  As a Customer, I want the capability to decline payment request for a specified amount requested by the merchant

  Condition of Satisfaction

  1.There should be an option to decline the payment request
  2.Merchant should receive the notification for declined payment request.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "21687970"
    And I enter login PIN "970541"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    And I click back button on Membership plans page
    Then I am redirected to the homepage

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am redirected to the homepage
    When I click on the Merchant option
    Then I should see a list of merchants I have transacted with in the last 90 days

  Scenario: Searching Merchant by trading name
    When I click on the Search tab
    And I enter trading name as "qwerty"
    Then I should read a message stating "No Data Found"
    And I enter trading name as "Suhaas Kumar"
    Then I should see the Trading name along with Paymaart ID
    Then I select the Merchant "Suhaas Kumar TEST"
    Then I should be able to see the payment request made by the merchant in chat screen

  Scenario: Declining the Payment request
    When I see the payment request from the merchant
    And I click on Decline button
    Then I should see the status of payment request as declined
