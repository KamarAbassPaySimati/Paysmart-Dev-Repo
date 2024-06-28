Feature: Paymaart- Customer Android - Forgot PIN/Password
  As a registered customer of the platform, I want to be able to reset my PIN/Password in case I forget it so that I can regain access to my account securely.
  Condition of satisfaction
  The user should enter their registered email associated with the account
  If entered email is not as per registered email ,error will be shown
  After providing their email , the user should receive a OTP code via email to verify their identity.
  If the OTP is not received resend OTP option is given. And it is upto 3 attempts
  Users should be able to enter the received OTP in the designated field for verification.
  The system must validate the OTP, and upon successful validation, user will be redirected to answer any one security question to confirm user's identity
  Once identity is confirmed, user should be able to input a new 6-digit PIN of the user's choice. And the interface should ensure that the new PIN meets security requirements, such as length and complexity.
  Users must be provided with guidance on choosing a strong and secure PIN/password during the reset process.
  When the user confirms the new 6-digit PIN by entering it a second time, Then the application should verify that the new PIN matches the one entered previously.
  After successfully resetting the PIN/Password, the system should provide a confirmation message. and redirect to Login screen.

  Scenario: Update my Password
    Given The login screen is displayed
    When I select login with password
    When I click on forgot password
    Then I should be redirected to forgot password pin screen
    When I enter my email address as "harshith.kumar+2@7edge.com"
    And I click on proceed button for forgot password or pin
    Then I should be redirected to forgot password or pin OTP screen
    When I enter the forgot OTP as "355940"
    And I click on verify button for forgot password pin
    Then I should see error message "Invalid OTP" for field with ID "Forgot Password OTP Field" for forgot
    When I enter the forgot OTP as "355948"
    And I click on verify button for forgot password pin
    Then I should be redirected to confirm forgot Password screen
    When I enter the new password as "pass12"
    And I enter the confirm new password as "pass12"
    And I click on reset button for forgot password
    Then I should see error message "Weak password. Check Guide for strong passwords." for field with ID "New Password" for forgot password
    When I enter the new password as "Password@122"
    And I enter the confirm new password as "Password@123"
    And I enter the security answer as "BDD Test123" for password
    And I click on reset button for forgot password
    Then I should see error message "Password does not match" for field with ID "Confirm Password" for forgot password
    When I enter the new password as "Password@123"
    And I click on reset button for forgot password
    Then I should see error message "Invalid Security Question Answer" for field with ID "Security Answers" for forgot password
    And I enter the security answer as "x" for password
    And I click on reset button for forgot password
    Then I should view text Password updated successfully

  Scenario: Update my PIN
    Given The login screen is displayed
    When I click on forgot PIN
    Then I should be redirected to forgot password pin screen
    When I enter my email address as "harshith.kumar+2@7edge.com"
    Then I click on proceed button for forgot password or pin
    Then I should be redirected to forgot password or pin OTP screen
    When I enter the forgot OTP as "355940"
    And I click on verify button for forgot password pin
    Then I should see error message "Invalid OTP" for field with ID "Forgot Password OTP Field" for forgot
    When I enter the forgot OTP as "355948"
    And I click on verify button for forgot password pin
    Then I should be redirected to confirm forgot pin screen
    When I enter the new PIN as "123456"
    And I enter the confirm new PIN as "123456"
    And I enter the security answer as "BDD Test" for pin
    And I click on reset button for forgot pin
    Then I should see error message "Weak PIN. Check Guide for strong PIN." for field with ID "New PIN" for forgot pin
    When I enter the new PIN as "976431"
    And I enter the confirm new PIN as "976431"
    And I enter the security answer as "BDD Test" for pin
    And I click on reset button for forgot pin
    Then I should see error message "Invalid Security Question Answer" for field with ID "Security Answer" for forgot pin
    And I enter the security answer as "x" for pin
    And I click on reset button for forgot pin
    Then I should view text PIN updated successfully