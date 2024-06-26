Feature: Paymaart- Customer Android- Update PIN/Password
  As a Customer , I want to update my PIN/Password , so that I need not use the credentials provided by system
  Condtion of satisfaction
  Upon navigating to settings user should be given an option Update PIN/Password
  There should be an option to select PIN or Password
  Users should have fields to enter CurrentPIN/Password, new PIN/Password and Confirm PIN/Password.The system must validate the Current PIN/Password, and upon successful validation, the user's identity is confirmed.
  Users must be provided with guidance on choosing a strong and secure PIN/Password during the reset process.
  Users should receive an error message upon incorrect Old PIN/Password entry
  The system should throw error if New PIN/Password and COnfirm Pin/Password doesnot match
  After successfully updating the PIN/Password, the system should provide a confirmation message. and global logout should happen and user should be rediirected to login screen
  User can update from PIN to password or vice versa.

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
    When I click the back button in kyc progress screen
    And I should be redirected to home screen
    When I open menu and navigate to the update PIN or password
    When I enter user PIN "" for PIN or Password
    And I click on update button for PIN or Password
    Then I should see error message "Required field" for field with ID "Current PIN/Password" for updating PIN or Password
    When I enter user PIN "123456" for PIN or Password

  Scenario: Customer updating Pin
    When I enter the new PIN as "111111" for updating PIN
    And I enter the confirm new PIN as "111111" for updating PIN
    And I click on update button for PIN or Password
    Then I should see error message "Weak PIN. Check Guide for strong PIN." for field with ID "New PIN" for updating PIN or Password
    When I enter the new PIN as "104538" for updating PIN
    And I enter the confirm new PIN as "104538" for updating PIN
    And I click on update button for PIN or Password
    Then I should read a message stating "PIN Updated"

  Scenario: Customer updating Password
    When I choose password section for update
    When I enter the new password as "password" for updating password
    And I enter the confirm new password as "password" for updating password
    And I click on update button for PIN or Password
    Then I should see error message "Weak password. Check Guide for strong passwords." for field with ID "New Password" for updating PIN or Password
    When I enter the new password as "7Edge@2024" for updating password
    And I enter the confirm new password as "7Edge@2024" for updating password
    And I click on update button for PIN or Password
    Then I should read a message stating "Password Updated"