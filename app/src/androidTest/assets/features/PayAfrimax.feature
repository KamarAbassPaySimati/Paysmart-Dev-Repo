Feature: Paymaart- Customer Android- Pay Afrimax
    As a customer, I want to be able to make payments to Afrimax (for telecom services) .
    Condition of satisfaction
    The user should be having a field to enter the Afrimax ID
    Upon entering the ID, the Afrimax name along with the entered Afrimax ID should be shown to confirm user identity
    The user should be able to change the Afrimax ID if entered incorrectly.
    There be an error shown if entered afrimax is invalid or not present in the system
    Upon Confirming the entered Afrimax details are correct, the user should be able to enter the amount directly or choose from any Afrimax plan/details that are pulled from Spynx API.
    Upon choosing the right details, payment is confirmed by entering the PIN/Password.
    There should be an display of transaction fee, VAT included and total payable amount
    User should have an option to continue with the payment or cancel the payment at any screen before entering the PIN/Password
    The proof of payment should be generated using a standardized Paymaart template.
    Users should have the option to share the transaction details in WhatsApp, email, or SMS.
    NOTE: Once confirmed, credits sent to Afrimax cannot be reversed.
    Notifications

Background: Logging into admin approved agent account
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

  Scenario: Making payments to afrimax
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

  Scenario: Making payments through choosing Plans
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