Feature: Paymaart- Customer Android- Flag Transaction
  As a Customer , I should be able to flag the transactions ,so that admin can
  review and do the needful according to the flag reason
  Condition of satisfaction
  1.There should be an option to select the reason from the predefined list for
  flagging a transaction
  2.There should be an option to select to make multiple selection from the predefined
  list
  After the successful submission the request goes to the admin for review.

  Background: Register new user and navigate to home screen
    Given I am in registration screen
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I enter a valid email address for registration
    When I enter a valid phone number for registration
    When I answer the security question one as "Answer1"
    When I answer the security question two as "Answer2"
    When I answer the security question three as "Answer3"
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
    When I click on submit button for national id
    When I click on biometric live selfie
    And I click on capture my live selfie and click on submit button
    When I select the Verification document as "Employer letter"
    And I capture front of the document and click on looks good
    Then I should be able to view the preview of the document front
    When I click on submit button for Employer Letter
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
    When I should view monthly income and monthly withdrawal selected as "Up to 300,000.00 MWK"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In review"
    When I click on the finish button
    Then I should be redirected to home screen

  Scenario: Flag a afrimax transaction through entering amount
    When I click on pay to afrimax
    When I click the continue button for pay to afrimax
    Then I should see error message "Required field" for field with ID "Afrimax ID" for pay to afrimax
    When I enter afrimax ID "11700"
    When I click the continue button for pay to afrimax
    Then I should see error message "Invalid Afrimax Id" for field with ID "Afrimax ID" for pay to afrimax
    When I enter afrimax ID "11729"
    When I click the continue button for pay to afrimax
    Then I should see prefilled field for Afrimax name
    When I click on the Re-enter button for Afrimax ID
    When I enter afrimax ID "11729"
    When I click the continue button for pay to afrimax
    Then I should see prefilled field for Afrimax name
    When I click the continue button for pay to afrimax screen 2
    Then I should be redirected to send payment screen
    When I click on Send Payment button for pay to afrimax
    Then I should see error message "Please enter amount" for field with ID "Amount" for pay to afrimax
    When I enter amount as "0.88" for pay to afrimax
    When I click on Send Payment button for pay to afrimax
    Then I should see error message "Minimum amount is 1.00 MWK" for field with ID "Amount" for pay to afrimax
    When I enter amount as "500000" for pay to afrimax
    When I click on Send Payment button for pay to afrimax
    Then I should see a popup for proceeding payment for pay to afrimax
    When I click on proceed button for payment
    Then I should be asked for the login pin for payment
    When I click on the confirm button for login pin
    Then I should see error message "Required field" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123451" for payment
    When I click on the confirm button for login pin
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123451" for payment
    When I click on the confirm button for login pin
    Then I should see error message "Insufficient funds" for field with ID "Amount" for pay to afrimax
    When I enter amount as "100" for pay to afrimax
    When I click on Send Payment button for pay to afrimax
    Then I should see a popup for proceeding payment for pay to afrimax
    When I click on proceed button for payment
    Then I should be asked for the login pin for payment
    When I click on the confirm button for login pin
    Then I should see error message "Required field" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123451" for payment
    When I click on the confirm button for login pin
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123456" for payment
    When I click on the confirm button for login pin
    Then I should read a message stating "Payment Successful" for payment
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on flag transaction button
    Then I should see error message "Select at least 1"
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should see flag transaction button disabled

  Scenario: Flag a afrimax transaction through selecting plan
    When I click on pay to afrimax
    When I click the continue button for pay to afrimax
    Then I should see error message "Required field" for field with ID "Afrimax ID" for pay to afrimax
    When I enter afrimax ID "11700"
    When I click the continue button for pay to afrimax
    Then I should see error message "Invalid Afrimax Id" for field with ID "Afrimax ID" for pay to afrimax
    When I enter afrimax ID "11729"
    When I click the continue button for pay to afrimax
    Then I should see prefilled field for Afrimax name
    When I click on the Re-enter button for Afrimax ID
    When I enter afrimax ID "11729"
    When I click the continue button for pay to afrimax
    Then I should see prefilled field for Afrimax name
    When I click the continue button for pay to afrimax screen 2
    Then I should be redirected to send payment screen
    When I select Choose Plan tab
    And I click on first plan in list
    When I click on Send Payment button for pay to afrimax
    Then I should see a popup for proceeding payment for pay to afrimax
    When I click on proceed button for payment
    Then I should be asked for the login pin for payment
    When I click on the confirm button for login pin
    Then I should see error message "Required field" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "736910" for payment
    When I click on the confirm button for login pin
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123456" for payment
    When I click on the confirm button for login pin
    Then I should read a message stating "Payment Successful" for payment
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on flag transaction button
    Then I should see error message "Select at least 1"
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should see flag transaction button disabled

  Scenario: Flag a pay to paymaart transaction for prime membership
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
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on flag transaction button
    Then I should see error message "Select at least 1"
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should see flag transaction button disabled

  Scenario: Flag a pay to paymaart transaction for primeX membership
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    When I click on the Buy button for PrimeX membership
    And I select 91 days membership
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "PrimeX Membership" details screen
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
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on flag transaction button
    Then I should see error message "Select at least 1"
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should see flag transaction button disabled