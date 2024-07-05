Feature: Paymaart- Customer Android- View Wallet Balance
  As a customer, I want to be able to view my wallet balance so that I can keep track of the funds available in my account.
  Condition of satisfaction
  The wallet balance should be updated in real-time to reflect any changes due to transactions, deposits, or withdrawals.
  The currency in which the balance is presented should be clearly indicated.
  The balance display should include additional details, such as the date and time of the last update.
  Currency is shown to 2 decimal places and is in MWK.
  The wallet balances are only visible after PIN/Password entry. And not immediately upon opening the mobile app.

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
    When I agree to the terms and conditions
    When I click on verify email address
    When I enter the valid OTP and verify
    Then I should see the verify email address button text changed to "VERIFIED"
    When I click on verify phone number
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
    When I select malawi simplified kyc
    Then I am redirected to KYC screen one
    When I enter the street name as "Balaka"
    Then I should see the district and town getting prefilled
    When I click on proceed button on screen one
    Then I should be redirected to KYC screen two
    When I click on proceed button on screen two
    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
    When I select the ID document as "Drivers licence"
    And I capture front of the document and click on looks good
    And I capture back of the document and click on looks good
    Then I should be able to view the preview of the document front and back
    When I click on submit button for drivers licence
    When I click on biometric live selfie
    And I click on capture my live selfie and click on submit button
    When I select the Verification document as "Employer letter"
    And I capture front of the document and click on looks good
    Then I should be able to view the preview of the document front
    When I click on submit button for employer letter
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
    Then I should view monthly income and monthly withdrawal selected as "Up to 300,000.00 MWK"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In review"
    Then I make the user approved
    When I click on the finish button
    And I should be redirected to home screen

  Scenario: Agent viewing wallet balance
    When I click on view wallet balance icon
    Then I should be asked for the login pin for viewing wallet balance
    When I click on view balance button
    Then I should see error message "Required field" for viewing wallet balance
    When I enter authorisation PIN "234567" for viewing wallet balance
    When I click on view balance button
    Then I should see error message "Invalid PIN" for viewing wallet balance
    When I enter authorisation PIN "123456" for viewing wallet balance
    When I click on view balance button
    Then I should see the wallet balance