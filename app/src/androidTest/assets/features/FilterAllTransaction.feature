Feature: Paymaart- Customer Android-Filter Transactions

  As a customer, I want to see a complete list of all my transactions for reference and tracking.

  Condition of satisfaction
  there should be an option to search using Paymaart Name/Paymaart ID
  Users should be able to see complete list of all transactions with various customers(registered and unregistered),merchants,paymaart,agents,afrimax,interest,refund,cash-out,cash-in,pay-in for reference and tracking
  3.There should be an option to filter using Time period and transaction type and sort the transaction.
  There should be pagination for the list
  5.The list to be shown for last 60days by default
  There should be an option to select Multiple filtering for transaction type

  Background: I navigate to home screen
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should be redirected to transaction history screen

  Scenario Outline: I apply the filter
    When I click on filter button
    Then I should see the filter pop up
    When I select the filter type as <filter type>
    When I select the filter as <filter>
    When I click on apply button
    Examples:
      |filter type        |filter        |
      |"timePeriod"       |"today"       |
      |"timePeriod"       |"yesterday"   |
      |"timePeriod"       |"last 7 days" |
      |"timePeriod"       |"last 30 days"|
      |"transactionType"  |"cash-in"     |
      |"transactionType"  |"cash-out"    |
      |"transactionType"  |"Pay Afrimax" |
      |"transactionType"  |"Pay Person"  |

  Scenario: I clear all the filter
    When I click on filter button
    Then I should see the filter pop up
    When I select the filter type as "timePeriod"
    When I select the filter as "yesterday"
    When I select the filter as "today"
    When I click on clear all button
