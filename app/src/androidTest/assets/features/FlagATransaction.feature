Feature: Paymaart- Customer Android- Flag Transaction
  As a Customer , I should be able to flag the transactions ,so that admin can
  review and do the needful according to the flag reason
  Condition of satisfaction
  1.There should be an option to select the reason from the predefined list for
  flagging a transaction
  2.There should be an option to select to make multiple selection from the predefined
  list
  After the successful submission the request goes to the admin for review.

  Background: Register new user and navigate to home screen
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP

  Scenario: Flag a pay to paymaart transaction for prime membership
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for Prime membership
    And I select 30 days membership
    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "Prime Membership" details screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should click on proceed button
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Required field" for field with ID "Login Pin" for membership selection
    When I enter login PIN "965274" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should be redirected to payment successful screen
    Then I should read a message stating "Payment Successful" for membership selection
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should be redirected to payment successful screen

  Scenario: Flag a pay to paymaart transaction for primeX membership
    Then I should be redirected to home screen
    When I click on pay to paymaart
    Then I should be redirected to membership plans screen
    Then I should read "Choose Prime, for totally free e-payments" above the membership banner
    When I click on the Buy button for PrimeX membership
    And I select 30 days membership
    And I select Auto-renewal
    When I click on the Submit button for membership selection screen
    Then I should be redirected to "PrimeX Membership" details screen
    When I click on the send payment button for membership selection
    When I should see a popup for proceeding membership
    Then I should click on proceed button
    Then I should be asked for authorisation pin for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should see error message "Required field" for field with ID "Login Pin" for membership selection
    When I enter login PIN "965274" for membership selection
    When I click on the confirm button for login pin for membership selection
    Then I should be redirected to payment successful screen
    Then I should read a message stating "Payment Successful" for membership selection
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should be redirected to payment successful screen

  Scenario: Flag a afrimax transaction through entering amount
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
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should be redirected to payment successful screen

  Scenario: Flag a afrimax transaction through selecting plan
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
    When I enter login PIN "965274" for payment
    When I click on the confirm button for login pin
    Then I should be redirected to payment successful screen
    Then I should read a message stating "Payment Successful" for payment
    #
    When I click on flag transaction icon
    Then I should be redirected to select reasons screen
    When I click on first reason for flag transaction
    When I click on flag transaction button
    Then I should see a flag transaction success pop up
    When I click on done button for flag transaction
    Then I should be redirected to payment successful screen