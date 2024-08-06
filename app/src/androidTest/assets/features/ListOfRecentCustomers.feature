Feature: Paymaart- Customer iOS- List of Recent Transacted Customer
  As a customer , I want an option to see the list of recent transaction with the customer
  Condition of satisfaction
  The user should have the option to view the recent transactions listing with the customer for last 60 days.
  Information to be displayed:
  Listing screen
  1.Paymaart Name
  2.Paymaart ID
  3.Phone number
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

  Scenario: I search for registered customer with paymaart ID
    When I search ""
    Then I should read a message stating "Search for Customer"
    When I search "00000000"
    Then I should read a message stating "No Data Found"
    When I search for "42163422"
    Then I should see the list of customers
    When I click on the first profile of customer
    Then I should be redirected to customer transaction screen
    When I click on send payment button
    Then I should be redirected to send payment screen
    When I click on Send Payment button for pay to afrimax
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
    When I click on cancel button
    Then I should be redirected to home screen
    When I click on pay to person
    Then I should be redirected to pay to person screen
    Then I should see the recently transacted customer transaction

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
    When I click on cancel button
    Then I should be redirected to home screen
    When I click on pay to person
    Then I should be redirected to pay to person screen
    Then I should see the recently transacted customer transaction