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

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "81133389"
    And I enter login PIN "529106"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
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
    When I enter amount as "11000000" for pay to afrimax
    When I click on Send Payment button for pay to afrimax
    Then I should see a popup for proceeding payment for pay to afrimax
    When I click on proceed button for payment
    Then I should be asked for the login pin for payment
    When I click on the confirm button for login pin
    Then I should see error message "Required field" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "123451" for payment
    When I click on the confirm button for login pin
    Then I should see error message "Invalid PIN" for field with ID "Login Pin" for pay to afrimax
    When I enter login PIN "529106" for payment
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
    When I enter login PIN "529106" for payment
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
    When I enter login PIN "529106" for payment
    When I click on the confirm button for login pin
    Then I should read a message stating "Payment Successful" for payment