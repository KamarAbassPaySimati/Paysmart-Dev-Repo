Feature: Paymaart- Customer Android- Edit Self KYC Details(Full)
  As a Customer ,I want an option to Edit existing KYC detail
  Condition of satisfaction
  Each screen in the KYC details section should have the user KYC type and status shown.
  Upon clicking KYC details only name and paymaart ID should be visible.
  For viewing the full details including Basic details and KYC details,users need to enter the PIN/Password as per their login selection.
  For editing the KYC phone number and email user should be prompted with security question , upon successfully answering the question user will be able to make changes
  If any section is changed/edited the old values needs to be removed and new details should be added, upon admin confirming the approval on changes, those changes will be reflecting on the user KYC details.
  Upon slicking the Edit option , If the user is the simplified KYC member, then there should be an option to continue editing existing KYC or select Full KYC option.
  If user select the Full KYC option ,only the ID details and Income section should be displayed to add details, upload documents, remaining fields should be auto filed by system 5.Upon clicking edit KYC, basic details and KYC
  There should be an option to upload the profile picture from the phone gallery or camera.
  The uploaded picture as only to be visible in user end.
  Upon succesfully performing the updation of the user KYC, it should be submitted to the admin for verification.
  There should be an option to view user profile picture/ lozenge shape with first 3 letters from first name, middle name and surname in Caps \

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
    When I click on the finish button
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
    And I click on edit button for Kyc details screen
    Then I should be redirected to "personal details" screen

  Scenario: Customer editing KYC details
    And I should see prefilled fields for personal details screen
    When I update email field with ""
    And I update phone number field with ""
    And I click on save and continue button for editing personal details screen
    Then I should see error message "Required field" for field with ID "email" in personal details page
    Then I should see error message "Required field" for field with ID "phone number" in personal details page
    When I update email field with new email address
    And I update phone number field with new phone number
    And I click on save and continue button for editing personal details screen
    Then I should see error message "Please Verify Your Email Address" for field with ID "email" in personal details page
    Then I should see error message "Please Verify Your Phone Number" for field with ID "phone number" in personal details page
    When I update email field with new email address
    And I click on verify button for update email address
    And I enter the OTP as "123456" for email
    Then I should be asked for security question
    And I enter the security answer as "xyz" for email updation
    And I click on verify button for email verification
    Then I should see error message "Invalid OTP" for email
    When I enter the valid OTP for email
    And I enter the security answer as "xyz" for email updation
    And I click on verify button for email verification
    Then I should see error message "Invalid Security Question Answer" for field with ID "security answer" in personal details page
    And I enter the security answer as "Answer" for email updation
    And I click on verify button for email verification
    Then I should see the verify email address button text changed to "VERIFIED" for email in personal details screen
    And I update phone number field with new phone number
    And I click on verify button for update phone number
    When I enter the valid OTP for phone number
    Then I should be asked for security question
    And I enter the security answer as "Answer" for phone number updation
    And I click on verify button for phone number verification
    Then I should see the verify phone number button text changed to "VERIFIED" for phone number in personal details screen
    And I click on save and continue button for editing personal details screen
    Then I should be redirected to "Your Address" screen
    Then I should see prefilled fields for your address screen
    When I update the street name as "" for your address screen
    And I click on save and continue button for editing your address screen
    Then I should see error message "Required field" for field with ID "street name" in personal details page
    When I update the street name as "Gundani Road" for your address screen
    Then I should see the district and town getting prefilled for your address screen
    And I click on save and continue button for editing your address screen
    Then I should be redirected to "Your Identity" screen
    When I select the ID document as "National ID" for editing self kyc
    When I click remove front side of the document
    When I click remove back side of the document
    And I capture front of the document and click on looks good
    And I capture back of the document and click on looks good
    Then I should be able to view the preview of the document front and back
    When I click on submit button for national id
    When I click on biometric live selfie
    And I click on recapture my live selfie and click on submit button
    When I select the Verification document as "Birth Certificate"
    When I click remove front side of the document
    And I capture front of the document and click on looks good
    Then I should be able to view the preview of the document front
    When I click on submit button for birth certificate
    And I click on save and continue button for Identity screen
    Then I should be redirected to "Your Info" screen
    And I should see prefilled fields for your info screen
    Then I update the gender field to "female" for edit self kyc
    Then I update Occupation to others
    And I click on save and continue button for Your Info screen