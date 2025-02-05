Feature: Paymaart- Customer Android- Make Payments to Merchant

  As a Customer, I want to be able to make payments to a merchants

  Condition of satisfaction

  1. user should have an option to initiate a payment within the  application by entering the amount and comment.

  2. There should be an option to show Transaction fee,VAT included and Total amount

  3. User can proceed or cancel the payment before entering PIN/Password.

  4. Upon successful PIN/Password entry, the transaction detail is should displayed.

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

  Scenario: Clearing the result on Search tab
    When I click on clear button on search tab
    Then the search tab should be empty

  Scenario: Searching merchant by Paymaart ID
    When I click on the Search tab
    And I enter paymart ID "012345"
    Then I should read a message stating "No Data Found"
    When I click on clear button on search tab
    And I enter paymart ID "4263169"
    Then I should see the Trading name along with Paymaart ID
    When I select the Merchant "Suhaas Kumar TEST"
    Then I should see the merchant's name, ID and recent transaction history

  Scenario: Making a payment to Merchant
    When I click on Pay button
    Then I should be directed to send payment screen
    When I click on Send Payment
    Then I should read a message stating Please enter amount
    When I enter amount as "20" for pay to Merchant
    When I click on Send Payment
    Then I should read a message stating "Minimum amount is 100.00 MWK" in Send Payment Screen
    When I enter amount as "3796000" for pay to Merchant
    When I click on Send Payment
    And I click on Proceed
    And I enter Invalid PIN "102030"
    Then I should read a message stating "Invalid PIN" in PIN field
    When I enter Valid PIN "970541"
    Then I should read a message stating "Insufficient Funds." in Send Payment Screen
    When I enter amount as "300" for pay to Merchant
    Then I enter grocery in Add note tab
    When I click on Send Payment
    And I click on Proceed
    And I enter Invalid PIN "102030"
    Then I should read a message stating "Invalid PIN" in PIN field
    When I enter Valid PIN "970541"
    Then I should see the Payment Successful message along with details

  Scenario: Sharing the Transaction confirmation details
    Given I see Payment Successful message along with details
    When I click on Share icon
    Then I should see a popup window with different shareable options








