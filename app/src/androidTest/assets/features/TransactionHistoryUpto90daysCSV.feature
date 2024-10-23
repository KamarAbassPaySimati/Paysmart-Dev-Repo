Feature:Paymaart - Customer Android - Transaction histroy upto 90days(CSV)
  As a Customer, I want to view the last 90-day Transactions so I can download and have a detailed view
  Condition of Satisfaction:
  There should be the last 90-day transaction list in the recent transactions list
  There should be an option named Wallet statement in the Menu screen,
  Upon clicking the statement, It should have an option to select from the date range given.
  Upon selecting date options, it should be give a prompt to download using CSV or PDF
  Detailed information is to be downloaded in addition to the Customer closing balance on each transaction.

  Background: Logging into admin approved customer account
    Given The login screen is displayed
    When I choose to login with paymart ID
    When I enter paymart ID "92322529"
    And I enter login PIN "965274"
    When I click on login button
    Then I see the TOTP screen
    When I enter the generated OTP
    Then I should be redirected to home screen

  Scenario: Viewing and downloading list of transactions upto 90 days
    When I click on drop down for transactions
    Then I should see recent four transactions
    When I click on see all link
    Then I should see list of Transactions of last 90 days
    When I click the back button in transaction history screen
    Then I should be redirected to home screen
    When I open menu and navigate to Wallet Statement
    When I select "Last 90 days" as time period for downloading wallet statement
    And I click on export Wallet statement
    Then I should see a pop-up asking type of file to be downloaded
    When I select "CSV" as file type
    Then I should read a message stating Exported Successfully for downloading wallet statement



