Feature: Paymaart- Customer Android- Enable/Disable Auto Renew
  As a customer , I want the option to enable or disable the auto-renewal feature at any time.
  Condition of Satisfaction
  If a users has the auto-renewal feature enabled and there are sufficient funds in their Paymaart wallet at the time of renewal, the system should automatically renew their subscription.
  Users should receive a notification or confirmation message when their subscription is successfully renewed, indicating the next renewal date and any relevant information.
  If a Users subscription renewal fails due to insufficient funds or any other reason, they should receive a notification explaining the reason for the failure and instructions on how to update their payment method or add funds to their wallet.
  when during the autorenewal, if user purchased membership is 91 days and insufficient balance in user account, then system should check if money is available for 30days and deduct accordingly and user should be moved to 30days from then,If the money is insuffient for 30days, then membership will be moved to Go.
  The user should have an option to toggle between Autorenewal ON/OFF anytime.

  Background: register new user and navigate to kyc screen
    Given I am in registration screen
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I enter a valid email address for registration
    When I enter a valid phone number for registration
    When I answer the security question one as "Answer"
    When I answer the security question two as "Answer"
    When I answer the security question three as "Answer"
    When I answer the security question four as "Answer"
    When I agree to the terms and conditions
    When I submit the registration form
    Then I should see error message on email "Please Verify Your Email Address"
    Then I should see error message on phone "Please Verify Your Phone Number"
    When I click on verify email address
    And I enter the OTP as "001234"
    And I click on verify OTP
    Then I should see error message "Invalid OTP" on verify
    When I enter the valid OTP and verify
    Then I should see the verify email address button text changed to "VERIFIED"
    When I click on verify phone number
    And I enter the OTP as "001234"
    And I click on verify OTP
    Then I should see error message "Invalid OTP" on verify
    When I enter the valid OTP and verify
    Then I should see the verify phone number button text changed to "VERIFIED"
    When I submit the registration form
    Then I should read a message stating registration successfully
    When I click on go to login
    When I choose to login with phone number
    When I enter my previously registered phone number
    When I enter login PIN "123456"
    When I click on login button
    Then I should see the 2FA screen
    When I copy the key and proceed
    Then I see the TOTP screen
    When I enter the generated OTP
    When I click the finish button
    Then I am redirected to the kyc journey
    When I select complete kyc
    Then I am redirected to kyc select screen
    When I select malawi full kyc
    Then I am redirected to KYC screen one
    When I enter the street name as "Balaka"
    Then I should see the district and town getting prefilled
    When I click on proceed button on screen one
    Then I should be redirected to KYC screen two
    When I click on proceed button on screen two
    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
    When I select the ID document as "National ID"
    And I capture front of the document and click on looks good
    And I capture back of the document and click on looks good
    Then I should be able to view the preview of the document front and back
    When I click on submit button for national id
    When I click on biometric live selfie
    And I click on capture my live selfie and click on submit button
    When I select the Verification document as "Birth Certificate"
    And I capture front of the document and click on looks good
    Then I should be able to view the preview of the document front
    When I click on submit button for birth certificate
    When I click on proceed button on screen two
    Then I should be redirected to KYC screen three
    When I click on proceed button on screen three
    Then I should see error message "Required field" for field with ID "gender" in kyc screen
    Then I should see error message "Required field" for field with ID "dateOfBirth" in kyc screen
    Then I should see error message "Required field" for field with ID "occupation" in kyc screen
    When I select the gender as "Male"
    When I select the date of birth as "04-04-1999"
    When I click on occupation source of funds
    Then I should be redirected to occupation source of funds selection screen
    When I select the occupation type as "Employed"
    When I click on next button
    Then I should be able to view option to enter employer name
    When I enter the employer name as "Healthcare"
    When I select the industry sector as "Healthcare service"
    When I enter the district as "Dowa"
    When I select purpose of relation "1"
    When I select my monthly income as "300,000.00 to 1,000,000.00 MWK"
    When I select my monthly withdrawal as "300,000.00 to 1,000,000.00 MWK"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In review"
    Then I make the user approved
    When I click on the finish button
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click the back button in membership banner screen
    Then I should be redirected to home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on the Buy button for Prime membership
    And I select 30 days membership
    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "Prime Membership" details screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Requiured field" for field with ID "Login Pin" for membership selection
    When I enter login PIN "121212" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for membership selection
    When I enter login PIN "123456" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should read a message stating "Payment Successful" for membership selection
    When I click on close icon for Transaction Details screen
    Then I should be redirected to home screen

  Scenario: Disabling auto-renew of membership
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on toggle button to "disable" auto renew
    Then I should see a pop up asking confirm auto renewal off
    When I click on confirm button for auto renewal off
    Then I should see toggle button getting disabled

  Scenario: Enabling auto-renewal for membership for Renew on  Expiry
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on toggle button to "disable" auto renew
    Then I should see a pop up asking confirm auto renewal off
    When I click on confirm button for auto renewal off
    Then I should see toggle button getting disabled
    And I should be redirected to membership plans screen
    When I click on toggle button to "enable" auto renew
    Then I should be redirected to auto renewal options screen
    When I click on "Renew on Expiry" for auto renewal
    And I select 30 days membership
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "Prime Membership" details screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Requiured field" for field with ID "Login Pin" for membership selection
    When I enter login PIN "121212" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for membership selection
    When I enter login PIN "123456" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should read a message stating "Payment Successful" for membership selection

  Scenario: Enabling auto-renewal for membership for Activate Auto-renew
    Given I am in home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on toggle button to "disable" auto renew
    Then I should see a pop up asking confirm auto renewal off
    When I click on confirm button for auto renewal off
    Then I should see toggle button getting disabled
    And I should be redirected to membership plans screen
    When I click on toggle button to "enable" auto renew
    Then I should be redirected to auto renewal options screen
    When I click on "Activate Auto-renew" for auto renewal
    Then I should see toggle button getting enabled








