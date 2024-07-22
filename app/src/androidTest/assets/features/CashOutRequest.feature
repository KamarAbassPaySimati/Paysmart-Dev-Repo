Feature: Paymaart- Customer Android- Perform Cash-out Request
  As a Paymaart customer, I want to be able to perform wallet cash-out transactions with an agent so that I can conveniently manage my e-wallet balance.
  Condition of satisfaction
  Cash-in transaction is not managed in the Paymaart system, only the transaction history will be available for cash-in when an agent transfers e-money to the customer.
  For Cash-out users will go to an agent, in the system should give an option to search for an Agent using Paymaart ID/ name.
  To confirm agents identity Paymaart NAme and Paymaart ID to be shown, Upon confirming the agent’s identity, the user should have an option to enter the amount (that is less than or equal to the wallet balance) and any note if applicable.
  User should enter the PIN/password to proceed with cash-out.
  The Paymaart agent should dispense the requested cash amount, and equivalent e-money to be deducted from the wallet.
  The proof of payment should be generated using a standardized Paymaart template.
  Users should be able to share the proof in WhatsApp, email, or SMS.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "81133389"
    And I enter login PIN "529106"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to membership plans screen
    When I select back button in Membership Screen
    Then I should be redirected to home screen

  Scenario: Customer requesting cash-out
  When I click on the cash-out button
  Then I should be redirected to recent cash-out screen
  And I search for agent with "33333" for requesting cash-out
  Then I should read a message stating "No Data Found" for requesting cash-out
  Then I should read a message stating "Try adjusting your search to find what you're looking for" for requesting cash-out
  When I search for agent with "249979" for requesting cash-out
  Then I should see a list of Agents for requesting cash-out
  When I click on first agent profile in list for requesting cash-out
  Then I should be redirected to complete cash-out screen
  When I click on the complete cash-out button
  Then I should see error message "Please enter amount" for field with ID "Amount" for requesting cash-out
  When I enter amount as "500000.00" for requesting cash-out
  When I click on the complete cash-out button
  Then I should see a popup for proceeding cash-out
  When I click on proceed cash-out button
  Then I should be asked for authorisation pin for requesting cash-out
  When I click on confirm button for requesting cash-out
  Then I should see error message "Required field" for field with ID "PIN" for requesting cash-out
  When I enter authorisation PIN "123456" for requesting cash-out
  When I click on confirm button for requesting cash-out
  Then I should see error message "Invalid PIN" for field with ID "PIN" for requesting cash-out
  When I enter authorisation PIN "736910" for requesting cash-out
  When I click on confirm button for requesting cash-out
  Then I should see error message "Insufficient funds" for field with ID "Amount" for requesting cash-out
  When I enter amount as "1000.00" for requesting cash-out
  When I click on the complete cash-out button
  Then I should see a popup for proceeding cash-out
  When I click on proceed cash-out button
  Then I should be asked for authorisation pin for requesting cash-out
  When I enter authorisation PIN "123456" for requesting cash-out
  When I click on confirm button for requesting cash-out
  Then I should see error message "Invalid PIN" for field with ID "PIN" for requesting cash-out
  When I enter authorisation PIN "736910" for requesting cash-out
  When I click on confirm button for requesting cash-out
  Then I should read a message stating "Cash-out Successful" after cash out


