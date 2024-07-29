Feature: Paymaart- Customer Android- Refund Payment Status

  As a Customer, I should be able to view all the refund payments status.

  Condition of satisfaction
  There should be an option to see a comprehensive list displaying all refund transactions requested.
  The user should be able to sort the refund payments list by date
  3.The user should be able to filter the refund payments list by status

  Background: I navigate to home screen
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen
    When I open menu and click on refund payment button
    Then I should be redirected to refund request screen

  Scenario Outline: Sort refund request according to date
    When I click on sort button
    Then I should see sort option pop up
    When I select the sort option as <sort>
      Examples:
      |sort|
      |"today" |
      |"yesterday"  |
      |"last 7 day" |
      |"last 30 day"|
      |"last 60 day"|

  Scenario Outline: I apply the filter
    When I click on filter button
    Then I should see the filter pop up
    When I select the filter as <filter>
    When I click on apply button
    Then I should see the transactions with filter <filter>
    Examples:
      |filter|
      |"refunded" |
      |"pending"  |
      |"rejected" |

  Scenario: I clear all the filter
    When I click on filter button
    Then I should see the filter pop up
    When I select the filter as "refunded"
    When I select the filter as "rejected"
    When I click on clear all button