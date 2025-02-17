Feature: Paymaart- Customer Android- Scan QRcode

  As a customer, I want the ability to make a purchase by scanning a QR code

  Condition of satisfaction

  1.There should bean option to activate the device's camera for scanning QR codes.
  2.The QR code scanner should efficiently capture QR codes in various formats, including those displayed on screens or printed.
  3.Upon successful QR code scanning, the application should interpret the QR code information, displaying Merchant details
  4.There should be a option to enter the Amount and Note/Comments if any, and confirm the payment.
  5.There should be an option to see Transaction fee based on membership, VAT included and total payable amount,
  6.There should be an cancel option at any screen before PIN/Password is entered.
  7.After confirming the purchase, user should receive a payment confirmation message, and the transaction should be recorded in user account's transaction histroy.
  8.In case of a payment failure,insufficient balance or an issue with the QR code, the application should provide clear and informative error messages.
  9.The proof of payment should be generated using a standardized Paymaart template.
  10.Users should be able to share the proof in WhatsApp, email, or SMS

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "25465668"
    And I enter login PIN "970541"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I am redirected to the homepage

  Scenario: Scanning the QR code
    Given I am in home screen
    When I click on Scan QR
    Then I see Scan any QR code screen
    When I scan the QR code
    Then I should be directed to send payment screen
    When I click on Back button
    Then I am redirected to the homepage
    When I click on Scan QR
    Then I see Scan any QR code screen
    When I scan the QR code
    Then I should be directed to send payment screen
    When I click on Send Payment
    Then I should read a message stating Please enter amount
    When I enter amount as "3796000" for pay to Merchant
    When I click on Send Payment
    When I click on Proceed
    When I enter Invalid PIN "102030"
    Then I should read a message stating "Invalid PIN" in PIN field
    When I enter Valid PIN "970541"
    Then I should read a message stating "Insufficient Funds." in Send Payment Screen
    When I enter amount as "300" for pay to Merchant
    Then I enter "grocery" in Add note tab
    When I click on Send Payment
    When I click on Cancel
    Then I should be directed to send payment screen
    When I click on Send Payment
    When I click on Proceed
    When I enter Invalid PIN "102030"
    Then I should read a message stating "Invalid PIN" in PIN field
    When I enter Valid PIN "970541"
    Then I should see the Payment Successful message along with details

  Scenario: Sharing the Transaction confirmation details
    Given I see Payment Successful message along with details
    When I click on Share icon
    Then I should see a popup window with different shareable options
