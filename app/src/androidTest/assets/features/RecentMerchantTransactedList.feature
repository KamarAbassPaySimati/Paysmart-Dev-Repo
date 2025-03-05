Feature: Paymaart- Customer Android- Recent merchant transacted list

  As a Customer, I should have an option to view the recently interacted Merchants,so that i can do a repeat transaction referring the list

  Condition of satisfaction

  1.There should be an option to view the listing of recently transacted merchants
  2.The list should consist of last 90 days transactions with merchant.
  3.There should an option to search the merchants using paymaart ID,trading name,till number, business type and city

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "25465668"
    And I enter login PIN "970541"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage

  Scenario: Viewing recently interacted Merchant transactions
    When I click on drop down for Merchants
    Then I should see recent four Merchants transactions
    When I click on see all
    Then I should be redirected to recently transacted merchants in last 90 days screen
    Then I click on Back button in Pay Merchant screen
    Then I am redirected to the homepage

  Scenario: Search for the Merchant
    When I click on drop down for Merchants
    Then I should see recent four Merchants transactions
    When I click on see all
    Then I should be redirected to recently transacted merchants in last 90 days screen
    When I search for ""
    Then I should read a message stating "No Transactions Yet" in pay Merchant screen
    When I search for "qwertytr"
    Then I should read a message stating "No Data Found" in pay Merchant screen
    When I search for "Suhaas Kumar"
    Then I should see the list of Merchants
        #When I click on the first profile of Merchant
        #Then I should be redirected to Merchant transaction screen 
