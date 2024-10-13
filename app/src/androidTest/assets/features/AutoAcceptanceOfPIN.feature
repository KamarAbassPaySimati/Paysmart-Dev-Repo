Feature: Paymaart - Customer Android - Auto Acceptance of PIN
  As an Customer, I want an option to auto acceptance of the PIN entry through out the application usage, so to avoid the multiple interaction.
  Conditions of Satisfaction
  when enter the PIN ,it should be auto-accepted and proceed to further steps.
  No changes are to be made to the Password sections
  Auto acceptance of PIN entry Upon the capture of 6 digits
  No changes to the Login screen PIN entry.

  Background: Login into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen

  Scenario: Agent viewing wallet balance
    When I click on view wallet balance icon
    Then I should be asked for the login pin for viewing wallet balance
    When I enter authorisation PIN "234567" for viewing wallet balance
    Then I should see error message "Invalid PIN" for viewing wallet balance
    When I enter authorisation PIN "965274" for viewing wallet balance
    Then I should see the wallet balance