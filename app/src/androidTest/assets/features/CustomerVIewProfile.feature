Feature: Paymaart- Customer Android- View profile
  As a customer, I want to view my profile details
  Condition of satisfaction
  There should be an option to view user Paymaart name, Paymaart ID,Membership tier.Paymaart member since (year)
  Paymaart ID (This is a unique identified for each customer merchant and agent and starts with 3 letters followed by digits. So, CMR8 digits for customer, AGT 6 digits for Agent, MCT7 digits for Merchant. and PMT 5 digits for Admin/Super Admin)

  Background: register new user and navigate to kyc screen
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

  Scenario: View profile information
    When I select back button
    Then I should be redirected to home screen
    Then I should view my name and paymaart ID starting with CMR
