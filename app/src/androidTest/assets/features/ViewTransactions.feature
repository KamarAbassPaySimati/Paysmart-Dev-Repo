Feature: Paymaart- Customer Android- View Specific Transaction

  As a customer, I want to see the complete details of a specific transaction to understand the specifics of a payment or purchase.

  Condition of satisfaction
  There should be an option to see the payment status
  For unregistered person payment If the phone number exists in the user's phonebook, it is displayed along with the saved name
  3.Transaction details should be share able through watsapp, email,pdf etc
  There should be an option to flag Payment sent for merchant,registered customer,afrimax,paymaart
  5.Only approved refund transaction details are shown

  Scenario: I navigate to home screen
    Given I am in main interface screen
    When I click on login button
    Then I should be redirected to login screen
    When I choose to login with paymaart id
    When I enter the paymaart id as "CMR81133389"
    When I enter the PIN as "529106"
    When I click on login button
    Then I should see the 2FA screen
    When I copy the key and proceed
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should be redirected to transaction history screen
    When I click on the first row of the transaction table
    Then I should be redirected to payment receipt screen