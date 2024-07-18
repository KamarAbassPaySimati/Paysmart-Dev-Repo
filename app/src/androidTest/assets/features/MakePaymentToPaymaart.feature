Feature: Paymaart- Customer Android- Make Payment to Paymaart
  As a customer, I want to be able to make payments to Paymaart (for membership fees).
  Condition of satisfaction
  The user should have the option to pay paymaart.
  There should be an option to see memberships with detailed benefits and purchasing amount.
  Upon selecting the paymaart membership, user should enter the PIN/password and make the payment.
  The proof of payment should be generated using a standardized Paymaart template.

  Background: Logging into admin approved agent account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP


  Scenario: Selecting prime Membership for 30 days
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for Prime membership
    And I select 30 days membership
    And I select Auto-renewal
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

  Scenario: Selecting prime Membership for 91 days
    Then I should be redirected to home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for Prime membership
    And I select 91 days membership
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

  Scenario: Selecting primeX Membership for 30 days
    Then I should be redirected to home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for PrimeX membership
    And I select 30 days membership
    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "PrimeX Membership" details screen
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

  Scenario: Selecting primeX Membership for 91 days
    Then I should be redirected to home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for PrimeX membership
    And I select 91 days membership
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "PrimeX Membership" details screen
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



