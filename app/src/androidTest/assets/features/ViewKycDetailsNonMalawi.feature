Feature: Paymaart- Customer Android- View KYC Details(Non-Malawi)
  As a Customer, I want an option to View KYC details
  Condition of satisfaction
  Each screen in the KYC details section should have the user KYC type and status shown.
  Upon clicking KYC details only name and paymaart ID should be visible.
  For viewing the full details including Basic details and KYC details,users need to enter the PIN/Password as per their login selection.

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
    When I select non malawi full kyc
    Then I am redirected to KYC screen one
    When I enter the street name as "Balaka"
    Then I should see the district and town getting prefilled
    Then I select "Afghan" as the nationality
    When I click on proceed button on screen one
    Then I should be redirected to KYC screen two
    When I click on proceed button on screen two
    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
    When I select the ID document as "Refugee ID"
    And I capture front of the document and click on looks good
    Then I should be able to view the preview of the document front
    When I click on submit button kyc document
    When I click on biometric live selfie
    And I click on capture my live selfie and click on submit button
    When I select the Verification document as "Drivers licence"
    And I capture front of the document and click on looks good
    And I capture back of the document and click on looks good
    Then I should be able to view the preview of the document front and back
    When I click on submit button kyc document
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
    When I click on the finish button
    Then I should be redirected to home screen

  Scenario: I should be able to view the KYC details
    Given I am in home screen
    When I open menu and navigate to the KYC reg details
    And I click on the view "KYC details"
    Then I should be asked for the login pin
    When I click on confirm authorisation
    Then I should see error message "Required field" for view kyc
    When I enter authorisation PIN "234567" for view kyc
    When I click on confirm authorisation
    Then I should see error message "Invalid PIN" for view kyc
    When I enter authorisation PIN "123456" for view kyc
    When I click on confirm authorisation
    When I click on the dropdown option of "Contact Details"
    Then I should see email and phone number
    When I click on the dropdown option of "Your Address"
    Then I should see the address details
    Then I should see Nationality
    Then I should see Malawi Address
    Then I should see International Address
    When I click on the dropdown option of "Your Identity"
    Then I should able to see "ID Documents"
    When I click on the view "ID document"
    Then I should see a preview of uploaded "Refugee ID Front.jpg"
    When I close full screen preview screen
    And I should able to see "Verification Document"
    When I click on the view "verification document"
    Then I should see a preview of uploaded "Driver’s Licence Front.jpg"
    When I close full screen preview screen
    And I should able to see "Live selfie"
    When I click on the view "selfie"
    Then I should see a preview of uploaded "Biometrics.jpg"
    When I close full screen preview screen
    When I click on the dropdown option of "Your Info"
    Then I should see gender and date of birth
    And I should see occupation details
    And I should see monthly withdrawal and monthly income
