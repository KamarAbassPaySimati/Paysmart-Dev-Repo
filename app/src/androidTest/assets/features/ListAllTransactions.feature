Feature: Paymaart- Customer iOS-List of all Transactions

  As a customer, I want to see a complete list of all my transactions for reference and tracking.

  Condition of satisfaction
  there should be an option to search using Paymaart Name/Paymaart ID
  Users should be able to see complete list of all transactions with various customers(registered and unregistered),merchants,paymaart,agents,afrimax,interest,refund,cash-out,cash-in,pay-in for reference and tracking
  3.There should be an option to filter using Time period and transaction type and sort the transaction.
  There should be pagination for the list
  5.The list to be shown for last 60days by default
  There should be an option to select Multiple filtering for transaction type

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen

  Scenario: List all transactions
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should be redirected to transaction history screen

  Scenario: I search for transaction with name
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should be redirected to transaction history screen
    When I search ""
    Then I should read a message stating "Search for Transaction"
    When I search "qwerty"
    Then I should read a message stating "No Data Found"
    When I search for "afrimax"
    Then I should see the list of afrimax transactions

  Scenario: I search for transaction with transaction ID
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should be redirected to transaction history screen
    When I search "" to pay person
    Then I should read a message stating "Search for Transaction"
    When I search "xyz00000000"
    Then I should read a message stating "No Data Found"
    When I search for "AFX20240719092318940535"
    Then I should see the searched transactions