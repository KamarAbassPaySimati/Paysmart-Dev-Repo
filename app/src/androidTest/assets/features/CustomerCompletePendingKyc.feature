Feature: Paymaart- Customer Android- Complete pending KYC
  As a Customer, I should be able to complete the pending KYC
  Condition of satisfaction
  As a registered user on paymaart, I should have an option to skip the KYC and complete it later
  If the KYC is Incomplete and the verification is pending, the system should prompt to complete the KYC
  For performing any transaction within paymaart, KYC needs to be completed and verified.
  Upon clicking any section of the homepage , KYC completion should be prompted

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
    When I select back button
    Then I should be redirected to home screen
    When I click on payment option
    Then I should see a popup for complete pending KYC
    When I click on complete pending KYC registration
    Then I am redirected to the kyc journey
    When I select complete kyc
    Then I am redirected to kyc select screen

  Scenario: Complete Malawi Full KYC with filling in the ID document section
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

  Scenario: Complete simplified KYC with filling in the ID document section
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

  Scenario: Complete Non-Malawi full KYC with filling in the ID document section
    When I select non malawi full kyc
    Then I am redirected to KYC screen one
    When I enter the street name as "Balaka"
    Then I should see the district and town getting prefilled
    Then I select "Afghan" as the nationality
    When I click on proceed button on screen one
    Then I should be redirected to KYC screen two
    When I click on proceed button on screen two
    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
    When I select the ID document as "Passport"
    And I capture front of the document and click on looks good
    And I capture back of the document and click on looks good
    Then I should be able to view the preview of the document front and back
    Then I should be able to view the preview of the document front
    When I click on submit button kyc document
    When I select the nature of permit as "Single/Multiple Entry Visa"
    And I enter the reference number as "8379128393"
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