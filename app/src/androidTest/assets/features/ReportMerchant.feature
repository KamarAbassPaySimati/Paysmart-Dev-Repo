Feature: Paymaart- Customer Android- Report Merchant

  As a Customer, I want the option to report a merchant

  Condition of satisfaction

  1.There should an option to report an merchant from the Merchant Profile details section
  2.There should be an option to select from the predefined list or enters a detailed description of the issue or concern related to the merchant.
   This could include specifics about the incident, date, time, and any supporting information.
  3.There should be an option to upload the images from device camera or gallery(max 2 images)
  4.Upon reporting a merchant request goes to Admin for further investigation

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "25465668"
    And I enter login PIN "970541"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage

  Scenario: Viewing recently transacted merchants from last 90 days
    Given I am redirected to the homepage
    When I click on the Merchant option
    Then I should see a list of merchants I have transacted with in the last 90 days

  Scenario: Searching Merchant by trading name
    When I click on the Search tab
    And I enter trading name as "qwerty"
    Then I should read a message stating "No Data Found"
    When I click on clear button on search tab
    Then the search tab should be empty
    When I click on the Search tab
    And I enter trading name as "Suhaas Kumar"
    Then I should see the Trading name along with Paymaart ID
    Then I select the Merchant "Suhaas Kumar TEST"

  Scenario: Viewing Merchant Profile
    Given I am on the chat screen
    When I click on the Merchant name
    Then I should be able to see the Merchant profile with details

  Scenario: Reporting a Merchant
    When I click on Report Merchant button in Merchant Profile screen
    And I click on Report Merchant button
    Then I should read a popup message stating "Please select at least one reason"
    When I select Merchant Disputes
    When I select Privacy Concerns
    When I select Others
    Then I should get a popup stating Please Specify
    When I click on Submit button
    Then I should not be able to Submit
    When I enter Non Puntual
    When I click on Submit button
    And I click on Report Merchant in Report Merchant screen
    Then I should see a popup stating "Merchant reported successfully"