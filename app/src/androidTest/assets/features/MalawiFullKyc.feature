Feature:Paymaart- Customer Android- Malawi FULL KYC
  As a Customer , I want an option to do Malawi full Kyc so that I can avail the services
  Condition of satisfaction
  Clear instructions and guidance should be provided on what documents are required and in which format.
  There should be an option to select whether an user is Malawi or Non-Malawi citizen
  If user is a Malawi then user should be having the option to select as Personal or as Business customer
  If user is a Personal customer there should be an option to select Full KYC or Simplified KYC
  The business Customer is an upcoming feature which needs to be shown with tag as coming soon
  Customers should be able to upload relevant documents, such as Location details ID details and Personal details, during the onboarding process, according to their KYC selection.
  The platform should have a progress indicator to show customers how far users are in the onboarding process.
  A live selfie should be captured according to predefined rules during the onboarding process.
  Customers should be able to save their progress and return to complete the onboarding if necessary.
  Upon successful submission of details the request goes to the admin for verification.
  If there are any issues with the submission, clear instructions on what needs to be corrected should be provided by admin.
  Email and SMS notifications should be sent to customers at different stages of the onboarding process, such as confirmation of submission and approval/rejection.
  The user should be able to see the documents to be submitted on each screen as information
  The user should upload document in any of the mentioned formats such as .png,.jpg.jpeg.gif from phone documents or camera
  Users should be able to skip to the next screen if required or come back to previous screen for any updates/changes.
  The location details are to be fetched from Google APIs.
  The user should mandatorily fill in street, town, and district under location details
  The user should be able to upload the front and back of the document(multiple uploads)
  User should be given the option to remove and reupload the documents.
  The system should show the user's the path chosen in KYC selection such as for eg:Malawi Citizen>Personal Customer>SImplified KYC

  Background: register new user and navigate to kyc screen
    Given I am in registration screen
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I enter a valid email address for registration
    Then I select Indian country code
    When I enter a valid phone number for registration
    When I answer the security question one as "Answer1"
    When I answer the security question two as "Answer2"
    When I answer the security question three as "Answer3"
    When I answer the security question four as "Answer4"
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

  Scenario: Missing required fields
    When I enter the street name as "" for KYC
    When I enter the town as "Lilongwe" for KYC
    When I click on proceed button on screen one
    Then I should see error message "Required field" for field with ID "streetName" in kyc screen

  Scenario: Complete KYC without filling in the ID document section
    When I enter the street name as "Balaka"
    Then I should see the district and town getting prefilled
    When I click on proceed button on screen one
    Then I should be redirected to KYC screen two
    When I click on proceed button on screen two
    Then I should see error message "Pending" for field with ID "selfieCapture" in kyc screen
    When I click on skip for KYC screen two
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
    When I select bank name as "CDH Investment Bank"
    When I enter the bank account number as ""
    When I enter the bank account name as ""
    When I click on proceed button on screen three
    Then I should see error message "Required field" for field with ID "bankAccountName" in kyc screen
    Then I should see error message "Required field" for field with ID "bankAccountNumber" in kyc screen
    When I enter the bank account number as "12938474234"
    When I enter the bank account name as "7Edge"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In progress"

  Scenario: Complete KYC with filling in the ID document section
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
    When I select bank name as "CDH Investment Bank"
    When I enter the bank account number as ""
    When I enter the bank account name as ""
    When I click on proceed button on screen three
    Then I should see error message "Required field" for field with ID "bankAccountName" in kyc screen
    Then I should see error message "Required field" for field with ID "bankAccountNumber" in kyc screen
    When I enter the bank account number as "12938474234"
    When I enter the bank account name as "7Edge"
    When I click on proceed button on screen three
    Then I should read a message stating KYC "In review"