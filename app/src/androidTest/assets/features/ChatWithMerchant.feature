Feature: Paymaart- Customer Android- Chat with Merchant

  As a customer, I want an option to chat with the merchant

  Condition of satisfaction

  1. there should be an option to chat with the merchants, and only text message are allowed.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "14018857"
    And I enter login PIN "742031"
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
    And I enter paymart ID as "012345"
    Then I should read a message stating "No Data Found" in Search Merchant page
    And I enter paymart ID "4263169"
    Then I should see the Trading name along with Paymaart ID

  Scenario: I try to chat with Merchant
    When I select the Merchant "Suhaas Kumar TEST"
    Then I should see the merchant's name, ID and recent transaction history
    When I click on Send icon
    Then no message will be sent
    And I click on "Enter Message" tab
    And I type "Hello"
    When I click on Send icon
    Then I should see the sent message in the screen
    
