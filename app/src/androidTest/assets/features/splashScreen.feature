Feature: Paymaart-Customer Android- Splash Screen Navigation
  As a customer, I want to see an informative and visually appealing splash screen when I open the app, so that I can quickly understand the app's identity and purpose.

  Scenario: Splash screen disappears after a few seconds
    Given The splash screen is displayed
    When I wait for a few seconds
    Then The main app interface should be displayed with text "Elevate your spending game with fast, secure, free e-payments"
    And I should see option to login
    And I should see option to register
