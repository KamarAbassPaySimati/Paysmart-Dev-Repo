Feature: Paymaart- Customer Android-Login
  As a Customer , I want an option to Login to the system
  Condition of satisfaction
  Users must be able to log in using their registered email/phone number/paymaart ID and PIN/Password
  After 3 attempts of incorrect PIN/password the system should temporarily lock the app for 10 minutes.
  If incorrect registered email/phone number/paymaart ID entered, an error should be shown
  If user have selected PIN as prior criteria and try to enter the password, an error should be shown asking to use PIN and vice versa
  The user should be able to do 2FA authentication with the code given and enter the 6 digit OTP received through the authenticator app in the application
  Upon successful entry if user is first time logging in user will be navigated to KYC screen
  If user is not first time logging in , then user will be navigated to the Home Screen

#  Scenario: Navigate to login screen
#    Given The Intro screen is displayed
#    Then The main app interface should be displayed with text "Elevate your spending game with fast, secure, free e-payments"
#    When I click the login customer button
#    Then I see the login screen
#
  Scenario: Enter Valid Customer Details and Verify Email Address
    Given I am in registration screen
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I enter a valid email address for registration
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
    When I enter the TOTP as "123456"
    Then I should see error message "Invalid Code"
    When I enter the generated OTP
    When I click the finish button
    Then I am redirected to the kyc journey

  Scenario: Login with Email
    Given The login screen is displayed
    When I choose to login with email address
    When I enter email address "harshith.kumar+2@7edge.com"
    And I enter login PIN "397618"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage

  Scenario: Login with Paymart ID and logout
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "72888403"
    And I enter login PIN "397618"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage
#    When I open menu and click on logout button
#    Then I should view confirmation popup
#    When I click on confirm logout button
#    Then I should be redirected to intro screen
#  The commented out code will be used in the further feature

