Feature: Paymaart- Customer Android - Delete Account
  As a customer of the application,I want to be able to delete my account permanently,So that I can remove all my personal information from the system and discontinue using the service.
  Condition of satisfaction
  There should be an option to navigate to the settings and select the option to delete the account.
  There should be predefined options to select for deleting the account from the list or entering free text
  upon confirmation, user account deletion will be sent to admin and after admin approves the account will be deleted from the system
  Once admin approves the request user should be logged out of all sessions, and all associated personal information and data should be removed from the system. (only the details as per RBM guidelines will be present for 7 years)
  Note: Delete Account request will be sent to admin for approval
  until the admin approves the user should have an option to use the application for any payments.

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

  Scenario: Customer deletes the account successfully
    When I open the menu
    And I click on settings and navigate to delete account
    Then I should be redirected to delete account screen
    And I should view the reasons for deleting the account
    When I select the reason for deletion as "I no longer need to use e-payment services"
    And I click on delete account button
    Then I should see a confirmation popup for delete account
    When I click on confirm button
    Then I should read a message stating deletion request sent successfully