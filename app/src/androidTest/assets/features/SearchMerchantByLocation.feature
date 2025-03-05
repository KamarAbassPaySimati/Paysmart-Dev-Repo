Feature: Paymaart- Customer Android- Search Merchant by location

  As a customer, I want to be able to search for merchants by location

  Condition of satisfaction

  1. Users should be able to search for merchants by trading type with location through the Paymaart platform.

  2. The search feature should respond once the entire character is filled

  3. Upon results shown on a searched merchant by user, there should be a option to see list of last 90 days transactions

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "85537845"
    And I enter login PIN "172654"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    And I click back button on Membership plans page
    Then I am redirected to the homepage

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am redirected to the homepage
    When I click on the Merchant option
    Then I should see a list of merchants I have transacted with in the last 90 days

  Scenario: Searching Merchant by Location using Filter
    When I click on Location icon
    And I click on Search tab
    And I enter the location as "Balaka"
    And I click on Filter
    And I select Hotels & Resorts as Trading type
    And I click Apply
    Then I should see the Merchants name along with location and Trading types

  Scenario: Searching Merchant by Location without using filter
    Given I go back to Pay Merchant page
    When I click on Location icon
    And I click on Search tab
    And I enter the location as "Balaka"
    Then I should see the Merchants name along with location and Trading types

  Scenario:  Searching Merchant just by selecting filter and not entering location
    Given I go back to Pay Merchant page
    When I click on Location icon
    And I click on Filter
    And I select Hotels & Resorts as Trading type
    And I click Apply
    Then I should read a message stating "No Data Found"


