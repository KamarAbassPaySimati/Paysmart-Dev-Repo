Feature: Paymaart- Customer Android- Recent Persons Transactions list
  As a Customer, I want to have an option to view the recently interacted Members/Peoples ,so that i can do a repeat transaction refering the list
  Condition of satisfaction
  1.There should be an option to view the listing of recently transacted members
  2.The list should consist of last 60 days transactions with customer.
  3.There should an option to search the customers using paymaart ID,Paymaart Name or telephone number
  for last 60 transactions
  Background: I navigate to home screen
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen

  Scenario: List all transactions
    When I click on drop down for persons
    Then I should see recent four persons
    When I click on see all link
    Then I should be redirected to recent transacted customer screen

  Scenario: Search for the customer
    When I click on drop down for persons
    Then I should see recent four persons
    When I click on see all link
    Then I should be redirected to recent transacted customer screen
    When I search ""
    Then I should read a message stating "Search for Customer"
    When I search "qwertytr"
    Then I should read a message stating "No Data Found"
    When I search for "swasthik"
    Then I should see the list of customers
        #When I click on the first profile of customer
        #Then I should be redirected to customer transaction screen