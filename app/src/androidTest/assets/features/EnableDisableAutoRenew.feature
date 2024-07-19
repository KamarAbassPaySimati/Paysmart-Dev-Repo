Feature: Paymaart- Customer Android- Enable/Disable Auto Renew
  As a customer , I want the option to enable or disable the auto-renewal feature at any time.
  Condition of Satisfaction
  If a users has the auto-renewal feature enabled and there are sufficient funds in their Paymaart wallet at the time of renewal, the system should automatically renew their subscription.
  Users should receive a notification or confirmation message when their subscription is successfully renewed, indicating the next renewal date and any relevant information.
  If a Users subscription renewal fails due to insufficient funds or any other reason, they should receive a notification explaining the reason for the failure and instructions on how to update their payment method or add funds to their wallet.
  when during the autorenewal, if user purchased membership is 91 days and insufficient balance in user account, then system should check if money is available for 30days and deduct accordingly and user should be moved to 30days from then,If the money is insuffient for 30days, then membership will be moved to Go.
  The user should have an option to toggle between Autorenewal ON/OFF anytime.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP


  Scenario: Disabling auto-renew of membership
    Then I should be redirected to membership plans screen
    When I click on the Buy button for Prime membership
    And I select 30 days membership
#    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "Prime Membership" details screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should click on proceed button
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Required field" for field with ID "Login Pin" for membership selection
#    When I enter login PIN "121212" for membership selection
#    When I click on the confirm button for login pin for membership selection
#    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for membership selection
    When I enter login PIN "965274" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should be redirected to payment successful screen
    Then I should read a message stating "Payment Successful" for membership selection
    When I click on close icon for Transaction Details screen
    Then I should be redirected to home screen
#    Then I should be redirected to membership plans screen
#    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
#    When I click on the Buy button for Prime membership
#    And I select 30 days membership
#    And I select Auto-renewal
#    When I click on the Submit button for membership selection screen
#    Then I should be redirected to "Prime Membership" details screen
#    When I click on the send payment button for membership selection
#    When I should see a popup for proceeding membership
#    Then I should be asked for authorisation pin for membership selection
#    When I click on the confirm button for login pin for membership selection
#    Then I should see error message "Required field" for field with ID "Login Pin" for membership selection
#    When I enter login PIN "121212" for membership selection
#    When I click on the confirm button for login pin for membership selection
#    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for membership selection
#    When I enter login PIN "965274" for membership selection
#    When I click on the confirm button for login pin for membership selection
#    Then I should read a message stating "Payment Successful" for membership selection
#    When I click on close icon for Transaction Details screen
#    Then I should be redirected to home screen
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on toggle button to "disable" auto renew
    Then I should see a pop up asking confirm auto renewal off
    When I click on confirm button for auto renewal off
    Then I should see toggle button getting disabled

  Scenario: Enabling auto-renewal for membership for Activate Auto-renew
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
#    When I click on toggle button to "disable" auto renew
#    Then I should see a pop up asking confirm auto renewal off
#    When I click on confirm button for auto renewal off
#    Then I should see toggle button getting disabled
    When I click on toggle button to "enable" auto renew
    Then I should be redirected to auto renewal options screen
    When I click on "Activate Auto-renew" for auto renewal
    Then I should see toggle button getting enabled

  Scenario: Enabling auto-renewal for membership for Renew on  Expiry
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
#    When I click on the Buy button for Prime membership
#    And I select 30 days membership
#    And I select Auto-renewal
#    Then I should see a pop up asking confirm auto renewal off
#    When I click on confirm button for auto renewal off
#    Then I should see toggle button getting disabled
#    And I should be redirected to membership plans screen
    When I click on toggle button to "disable" auto renew
    Then I should see a pop up asking confirm auto renewal off
    When I click on confirm button for auto renewal off
    Then I should see toggle button getting disabled
    When I click on toggle button to "enable" auto renew
    Then I should be redirected to auto renewal options screen
    When I click on "Renew on Expiry" for auto renewal
    And I select 30 days membership
    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should click on proceed button
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Required field" for field with ID "Login Pin" for membership selection
#    When I enter login PIN "121212" for membership selection
#    When I click on the confirm button for login pin for membership selection
#    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for membership selection
    When I enter login PIN "965274" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should read a message stating "Payment Successful" for membership selection









