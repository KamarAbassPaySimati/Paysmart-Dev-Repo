Feature: Paymaart- Customer Android- Make payments to Merchants request

  As a customer I want an option to make a payment requested by the merchant

  Condition of satisfaction

  1. there should be an option to see, merchant-sent payment request in the chat screen
  2. users should be given an option either to proceed with the payment or decline the request.
  3. There should be an option to show Transaction fee,VAT included and Total amount
  4. User can proceed or cancel the payment before entering PIN/Password.
  5. Upon successful PIN/Password entry, the transaction detail is should displayed.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "14018857"
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
    And I enter trading name as "Suhaas"
    Then I should see the Trading name along with Paymaart ID
    Then I select the Merchant "Suhaas Kumar TEST"
    Then I should be able to see the payment request made by the merchant in chat screen

  Scenario: Making a payment for a requested amount that exceeds the wallet balance
    When I click on Pay button for 1st Requested Amount "10000" in chat screen
    Then I should be directed to Send Payment page
    When I click on Send Payment button
    And I click on Proceed
    And I enter Invalid PIN as "102030"
    Then I should see a message as "Invalid PIN" in PIN field
    When I enter Valid PIN as "970541"
    Then I should see a message as "Insufficient Funds"
    Then I click on Back button

  Scenario: Making a payment for a requested amount less than the wallet balance
    When I click on Pay button for 2nd Requested Amount "2.80" in chat screen
    Then I should be directed to Send Payment page
    When I click on Send Payment button
    And I click on Proceed
    And I enter Invalid PIN as "102030"
    Then I should see a message as "Invalid PIN" in PIN field
    When I enter Valid PIN as "970541"
    Then I should read a message stating "Payment Successful" along with details.
    Then I click on Close button

  Scenario: Declining the Payment request
    Given I am on the Chat Screen
    When I click on Decline button for 3rd Requested Amount "56026" in chat screen
    Then I should see a message indicating declined for that Payment request





