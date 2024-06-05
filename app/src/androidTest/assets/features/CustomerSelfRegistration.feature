Feature:Paymaart- Customer Android- Self Registration
  As a customer, I want an option to register to the platform, so that I can be a Paymaart application member
  Conditions of satisfaction
  There should be an option to input their firstname, middle name, surname,email and phone number
  There should be an option to view and accept the terms and condition, and privacy policy
  There should a disclaimer given below email and phone number stating: enter the email and phone number correctly as a verification code will be sent.
  Users should receive an OTP (One-Time Password) via SMS to the provided mobile number for verification of phone number.
  Users should receive an OTP via a registered email address to confirm the verification of email
  If the Verification entered OTP is incorrect an error message needs to be displayed
  OTP resent option needs to be enabled after 2 min
  Users should complete security questions so that when update their phone number or email these questions are asked.The user should answer 3 questions out of 4 sets of questions provided.
  Users should receive a system-generated unique PIN through email once the Registration is completed, along with Paymaart ID, Email, and Phone number.
  Welcome message to be displayed on the screen from which the user can be navigated to the login screen
  There should be an option to select the country code
  Paymaart ID generation should be in sequential order
  There should be an option to upload a profile picture, which is optional from phone gallery or camera
  There should be an option to ask users whether the picture can be displayed publicly.
  Note: Resend can be allowed to 3 limit

  Scenario: Navigate through splash screen
    Given The splash screen is displayed
    When I wait for a few seconds
    Then The main app interface should be displayed with text "Elevate your spending game with fast, secure, free e-payments"
    And I should see option to login
    And I should see option to register

  Scenario: Navigate to registration screen
    Given I am in intro screen
    Given The main app interface should be displayed with text "Elevate your spending game with fast, secure, free e-payments"
    When I click on register customer
    Then I should be redirected to registration screen

  Scenario Outline: Register user with invalid details
    Given I am in registration screen
    When I enter the first name as <first_name> for registration
    When I enter the middle name as <middle_name> for registration
    When I enter the last name as <last_name> for registration
    When I enter the email address as <email> for registration
    Then I select Indian country code
    When I enter the phone number as <phone> for registration
    When I submit the registration form
    Then I should see error message <message> for field with ID <fieldID>

    Examples:
      | first_name | last_name | middle_name | email                    | phone        | message                            | fieldID      |
      | ""         | "Tyson"   | "D"         | "bharath.shet@7edge.com" | "9741292994" | "Required field"                   | "firstName"  |
      | "John"     | ""        | "Mike"      | "bharath.shet@7edge.com" | "9741292994" | "Required field"                   | "lastName"   |
      | "John"     | "Tyson"   | ""          | "bharath.shet@7edge.com" | "9741292994" | "Required field"                   | "middleName" |
      | "John"     | "Tyson"   | "D"         | ""                       | "9741292994" | "Required field"                   | "email"      |
      | "John"     | "Tyson"   | "D"         | "bharath.shet@7edge.com" | ""           | "Required field"                   | "phone"      |
      | "John"     | "Tyson"   | "D"         | "bharath.shet7edge.com"  | "9741292994" | "Invalid email"                    | "email"      |
      | "John"     | "Tyson"   | "D"         | "bharath.shet@7edge.com" | "9742994"    | "Invalid phone number"             | "phone"      |
      | "John"     | "Tyson"   | "D"         | "ajeeb@7edge.com"        | "9742994"    | "Please Verify Your Email Address" | "email"      |

  Scenario Outline: Answer security questions
    Given I am in registration screen
    When I answer the security question one as <answer_1>
    When I answer the security question two as <answer_2>
    When I answer the security question three as <answer_3>
    When I answer the security question four as <answer_4>
    When I submit the registration form
    Then I should see error message "Answer any 3" on registration screen
    Examples:
      | answer_1 | answer_2     | answer_3 | answer_4    |
      | "John"   | "i10"        | ""       | ""          |
      | ""       | "Lamborgini" | "Mike"   | ""          |
      | ""       | ""           | "Mike"   | "Bangalore" |
      | ""       | "Lamborgini" | ""       | "Bangalore" |
      | "John"   | ""           | ""       | "Bangalore" |
      | ""       | "Lamborgini" | ""       | "Malawi"    |

  Scenario: Validate already existing email address/phone number
    Given I am in registration screen
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I enter the email address as "abhishek.sreejith+test1@7edge.com" for registration
    When I click on verify email address
    Then I should see error message "Email already exists" for field with ID "email"
    When I enter a valid email address for registration
    When I click on verify email address
    When I enter the valid OTP and verify
    Then I should see the verify email address button text changed to "VERIFIED"
    Then I select Indian country code
    When I enter the phone number as "8192069218" for registration
    When I click on verify phone number
    Then I should see error message "Phone number already exists" for field with ID "phone"

  Scenario: Enter Valid Customer Details and Verify Email Address
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