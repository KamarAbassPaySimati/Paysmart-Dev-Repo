Feature: Paymaart- Customer Android- Make Payment to Unregistered User
  As a customer, I want to be able to make payments to a customer
  Condition of satisfaction
  The user should have an option to specify the payment amount and note/comment
  Upon successfully initiating the payment, by entering PIN/Password, the user should receive a confirmation message that includes details of the transaction.
  Both the sender and the recipient should receive notifications about the payment. An SMS to be sent to unregistered user(with a message to register for paymaart), and registered users will receive sms/email/puh notification based on the memberships
  The proof of payment should be generated using a standardized Paymaart template.
  Users should be able to share the proof of transactions in WhatsApp, email, or SMS
  The system should prompt the user to enter the recipient's phone number for an unregistered user payment.
  The system must have the capability to access and extract phone numbers in the correct format from the user's phone contacts list.
  Upon entering the phone number, the system should retrieve and display the associated Paymaart ID and recipient name, if they are registered member
  10. The system should mask the phone number for those members not available in user phone contacts, else phone number to be shown.
  11. The payment should be associated with the identified recipient based on the provided telephone number, even if they are not a registered Paymaart user.

  Background: I navigate to search pay person screen
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen
    When I click on pay to person
    Then I should be redirected to pay to person screen

  Scenario: I search for unregistered customer with phone number
    When I search ""
    Then I should read a message stating "Search for Customer"
    When I search "0000000000"
    Then I should read a message stating "No Data Found"
    When I search for "9988776655"
    Then I should see the list of customers
    When I click on the first profile of customer
    Then I should be redirected enter details of unregistered user
    When I click on confirm button
    Then I should see error message "Required Field" for field with ID "first name" for pay to afrimax
    When I enter a valid first name for registration
    When I enter a valid middle name for registration
    When I enter a valid last name for registration
    When I click on confirm button
    Then I should be redirected to send payment screen
    When I click on Send Payment button for pay to afrimax
    Then I should see error message "Please enter amount" for field with ID "Amount" for pay to afrimax
    When I enter amount as "0.88" for pay to afrimax
    When I click on Send Payment button for pay to afrimax
    Then I should see error message "Minimum amount is 1.00 MWK" for field with ID "Amount" for pay to afrimax
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
    When I enter login PIN "965274" for payment
    When I click on the confirm button for login pin
    Then I should be redirected to payment successful screen
    Then I should read a message stating "Payment Successful" for payment