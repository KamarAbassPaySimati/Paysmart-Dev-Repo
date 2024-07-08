Feature: Paymaart- Customer Android- Update to Full KYC
  As a Customer, I want an option to Update to FULL KYC

  Conditions of satisfaction

  Upon slicking the Edit option , If the user is the simplified KYC member, then there should be an option to continue editing existing KYC or select Full KYC option.

  7.If user select the Full KYC option ,only the ID details and Income section should be displayed to add details, upload documents, remaining fields should be auto filed by system 5.Upon clicking edit KYC, basic details and KYC

  10.Upon succesfully performing the updation of the user KYC, it should be submitted to the admin for verification.
  11.There should be an option to view user profile picture/ lozenge shape with first 3 letters from first name, middle name and surname in Caps

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
#  When I select my monthly income as "300,000.00 to 1,000,000.00 MWK"
#  When I select my monthly withdrawal as "300,000.00 to 1,000,000.00 MWK"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In review"
    Then I make the user approved
    When I click on the finish button
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I select back button in Membership Screen
    Then I should be redirected to home screen
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

#  Scenario: Upgrade simplified kyc registration to full kyc without filling id document
#    Given I should see the edit button enabled
#    When I click on edit button for Kyc details screen
#    Then I should see a pop to for upgrade to full kyc
#    When I click on upgrade to full kyc button
#    Then I should be redirected to customer KYC personal details screen
#    Then I should see prefilled fields for personal details screen
#    Then I should be redirected to "Your Address" screen
#    Then I should see address details to be prefilled and disabled to edit
#    When I click on proceed button on screen one
#    Then I should be redirected to KYC screen two
#    When I click on proceed button on screen two
#    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
#    When I click on skip for KYC screen two
#    Then I should be redirected to KYC screen three
#    Then I should see only income status to be editable
#    When I select my monthly income as "300,000.00 to 1,000,000.00 MWK"
#    When I select my monthly withdrawal as "300,000.00 to 1,000,000.00 MWK"
#    When I click on proceed button on screen three
#    Then I should read a message stating KYC "In review"
#    When I click on the finish button
#    Then I should be redirected to home screen
#    When I open menu and navigate to the KYC reg details
#    Then I should see kyc is upgraded to full

  Scenario: Upgrade simplified kyc registration to full kyc with filling id document
    Given I should see the edit button enabled
    When I click on edit button for Kyc details screen
    Then I should see a pop to for upgrade to full kyc
    When I click on upgrade to full kyc button
    Then I should be redirected to customer KYC personal details screen
    Then I should see prefilled fields for personal details screen
    When I click on save and continue button for editing personal details screen
    Then I should be redirected to "Your Address" screen
    Then I should see address details to be prefilled and disabled to edit
    And I click on save and continue button for editing your address screen
    Then I should be redirected to "Your Identity" screen
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
    And I click on save and continue button for Identity screen
    Then I should be redirected to "Your Info" screen
#  Then I should see only income status to be editable
    When I select my monthly income as "300,000.00 to 1,000,000.00 MWK"
    When I select my monthly withdrawal as "300,000.00 to 1,000,000.00 MWK"
    And I click on save and continue button for Your Info screen
    Then I should read a message stating Submission Successful
    When I click on the finish button on Edit Successful Screen
    Then I should be redirected to home screen
    When I open menu and navigate to the KYC reg details
    Then I should see kyc is upgraded to full





