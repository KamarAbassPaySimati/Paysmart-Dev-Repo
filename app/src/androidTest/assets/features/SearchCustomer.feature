Feature: Paymaart- Customer Android- Search Customer

  As a customer, I want an option to search for a customer

  Condition of satisfaction
  The user should have option to search the other user using paymaart ID,phone number and
  name
  The system must have the capability to access and extract phone numbers in the correct
  format from the user's phone contacts list.
  Upon entering the phone number, the system should retrieve and display the associated
  Paymaart ID and recipient name, if they are registered member
  The system should mask the phone number for those members not available in user phone
  contacts, else phone number to be shown.
  The payment should be associated with the identified recipient based on the provided
  telephone number, even if they are not a registered Paymaart user.

  Background: I navigate to search pay person screen
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
    When I click on pay to person
    Then I should be redirected to pay to person screen

  Scenario: I search for registered customer with name
    When I search "" to pay person
    Then I should read a message stating "Search for Customer" for pay person
    When I search "qwertytr" to pay person
    Then I should read a message stating "No Data Found" for pay person
    When I search for "swasthik" to pay person
    Then I should see the list of customers
        #When I click on the first profile of customer
        #Then I should be redirected to customer transaction screen

  Scenario: I search for registered customer with paymaart ID
    When I search "" to pay person
    Then I should read a message stating "Search for Customer" for pay person
    When I search "00000000" to pay person
    Then I should read a message stating "No Data Found" for pay person
    When I search for "42163422" to pay person
    Then I should see the list of customers
        #When I click on the first profile of customer
        #Then I should be redirected to customer transaction screen

  Scenario: I search for unregistered customer with contact phone number
    When I click on search contacts
    When I search "" to pay person
    Then I should read a message stating "Search for Customer" for pay person
    When I search "000000000" to pay person
    Then I should read a message stating "No Data Found" for pay person
    When I search for "9988776655" to pay person
    Then I should see the list of customers
        #When I click on the first profile of customer
        #Then I should be redirected to customer transaction screen

  Scenario: I search for unregistered customer with contact name
    When I click on search contacts
    When I search "" to pay person
    Then I should read a message stating "Search for Customer" for pay person
    When I search "qwertytr" to pay person
    Then I should read a message stating "No Data Found" for pay person
    When I search for "test10" to pay person
    Then I should see the list of customers
        #When I click on the first profile of customer
        #Then I should be redirected to customer transaction screen