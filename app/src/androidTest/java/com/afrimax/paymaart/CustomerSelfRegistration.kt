package com.afrimax.paymaart

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.afrimax.paymaart.R
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import net.datafaker.Faker
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class RegisterUserDefs {

    val faker = Faker()

    @Given("I am in intro screen")
    fun iAmInIntroScreen() {
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.intro_activity)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @When("I click on register customer")
    fun iClickOnRegisterCustomer() {
        Espresso.onView(withId(R.id.register_button)).perform(click())
    }

    @Then("I should be redirected to registration screen")
    fun iShouldBeRedirectedToRegistrationScreen() {
        Espresso.onView(withId(R.id.onboardRegistrationActivity)).check(matches(isDisplayed()))
    }

    @Given("I am in registration screen")
    fun iAmInRegistrationScreen() {
        Thread.sleep(7000)
        Espresso.onView(withId(R.id.register_button)).perform(click())
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.onboardRegistrationActivity)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @When("I enter the first name as {string} for registration")
    fun enterFirstName(firstName: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityFirstNameET))
            .perform(scrollTo(), replaceText(firstName), closeSoftKeyboard())
    }

    @When("I enter the middle name as {string} for registration")
    fun enterMiddleName(middleName: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityMiddleNameET))
            .perform(scrollTo(), replaceText(middleName), closeSoftKeyboard())
    }

    @When("I enter the last name as {string} for registration")
    fun enterLastName(lastName: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityLastNameET))
            .perform(scrollTo(), replaceText(lastName), closeSoftKeyboard())
    }

    @When("I enter the email address as {string} for registration")
    fun enterEmailAddress(emailAddress: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityEmailET))
            .perform(scrollTo(), typeText(emailAddress), closeSoftKeyboard())
    }

    @When("I enter the phone number as {string} for registration")
    fun enterPhoneNumber(phoneNumber: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneET))
            .perform(scrollTo(), replaceText(phoneNumber), closeSoftKeyboard())
    }
    @When("I enter a valid phone number for registration")
    fun enterValidPhoneNumber() {
        val phoneNumber = faker.phoneNumber()
            .subscriberNumber(10) // Replace with logic to generate a valid phone number
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneET))
            .perform(scrollTo(), typeText(phoneNumber), closeSoftKeyboard())
    }

    @Then("I should see error message {string} for field with ID {string}")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "firstName" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityFirstNameWarningTV))
                    .check(matches(withText(errorMessage)))
            }

            "middleName" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityMiddleNameWarningTV))
                    .check(matches(withText(errorMessage)))
            }

            "lastName" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityLastNameWarningTV))
                    .check(matches(withText(errorMessage)))
            }

            "email" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityEmailWarningTV))
                    .check(matches(withText(errorMessage)))
            }

            "phone" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneWarningTV))
                    .check(matches(withText(errorMessage)))
            }

            "security" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestionTV))
                    .check(matches(withText(errorMessage)))
            }

            "checkbox" -> {
                Espresso.onView(withId(R.id.onboardRegistrationActivityTnCPrivacyPolicyTV))
                    .check(matches(withText(errorMessage)))
            }
        }
    }

    @When("I submit the registration form")
    fun submitRegistrationForm() {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySubmitButton)).perform(click())
        Thread.sleep(5000)
    }

    @Then("I should read a message stating registration successfully")
    fun iShouldReadMessageStatingRegistrationSuccessfully() {
        Espresso.onView(withId(R.id.registrationSuccessfulActivityHeaderTV)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @When("I answer the security question one as {string}")
    fun answerSecurityQuestion1(answerOne: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestion1ET))
            .perform(scrollTo(), typeText(answerOne), closeSoftKeyboard())
    }

    @When("I answer the security question two as {string}")
    fun answerSecurityQuestion2(answerTwo: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestion2ET))
            .perform(scrollTo(), typeText(answerTwo), closeSoftKeyboard())
    }

    @When("I answer the security question three as {string}")
    fun answerSecurityQuestion3(answerThree: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestion3ET))
            .perform(scrollTo(), typeText(answerThree), closeSoftKeyboard())
    }

    @When("I answer the security question four as {string}")
    fun answerSecurityQuestion4(answerFour: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestion4ET))
            .perform(scrollTo(), typeText(answerFour), closeSoftKeyboard())
    }

    @Then("I should see error message {string} on registration screen")
    fun iShouldSeeErrorMessageAny3(errorMessage: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivitySecurityQuestionTV))
            .check(matches(withText(errorMessage)))
    }

    @Then("I should see error message {string} on verify")
    fun iShouldSeeErrorMessageOnVerify(errorMessage: String) {
        Espresso.onView(withId(R.id.registrationVerificationSheetWarningTV))
            .check(matches(withText(errorMessage)))
    }


    @When("I enter a valid first name for registration")
    fun enterRandomFirstName() {
        // Generate a random first name (implementation depends on your library)
        val firstName = faker.name().firstName()
        Espresso.onView(withId(R.id.onboardRegistrationActivityFirstNameET))
            .perform(scrollTo(), typeText(firstName), closeSoftKeyboard())
    }

    @When("I enter a valid middle name for registration")
    fun enterRandomMiddleName() {
        // Generate a random first name (implementation depends on your library)
        val middleName = "D" // Adjust length as needed
        Espresso.onView(withId(R.id.onboardRegistrationActivityMiddleNameET))
            .perform(scrollTo(), replaceText(middleName), closeSoftKeyboard())
    }

    @When("I enter a valid last name for registration")
    fun enterRandomLastName() {
        // Generate a random first name (implementation depends on your library)
        val lastName = faker.name().lastName() // Adjust length as needed
        Espresso.onView(withId(R.id.onboardRegistrationActivityLastNameET))
            .perform(scrollTo(), replaceText(lastName), closeSoftKeyboard())
    }


    @When("I enter a valid email address for registration")
    fun enterValidEmailAddress() {
        val firstName = "bharath"
        val lastName = "shet"
        val domain = "7edge.com" // Replace with your desired domain
        val randomNumber = faker.random().nextInt(100000, 999999) // Adjust range as needed

        val emailAddress = "$firstName.$lastName+$randomNumber@$domain"
        Espresso.onView(withId(R.id.onboardRegistrationActivityEmailET))
            .perform(scrollTo(), replaceText(emailAddress), closeSoftKeyboard())
    }

    @When("I agree to the terms and conditions")
    fun agreeToTAndC() {
        Espresso.onView(withId(R.id.onboardRegistrationActivityCB)).perform(scrollTo(), click())
    }

    @Then("I should see error message on email {string}")
    fun iShouldSeeErrorMessageOnEmail(errorMessage: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityEmailWarningTV))
            .check(matches(withText(errorMessage)))
    }

    @Then("I should see error message on phone {string}")
    fun iShouldSeeErrorMessageOnPhone(errorMessage: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneWarningTV))
            .check(matches(withText(errorMessage)))
    }

    @When("I click on verify email address")
    fun clickVerifyEmailAddress() {
        Espresso.onView(withId(R.id.onboardRegistrationActivityEmailVerifyButton))
            .perform(scrollTo(), click())
        Thread.sleep(5000)
    }

    @When("I click on verify phone number")
    fun clickVerifyPhoneNumber() {
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneVerifyButton))
            .perform(scrollTo(), click())
        Thread.sleep(5000)
    }

    @When("I enter the valid OTP and verify")
    fun enterAndVerifyPhoneOTP() {
        Espresso.onView(withId(R.id.registrationVerificationSheetCodeET))
            .perform(replaceText("355948"), closeSoftKeyboard())
        Espresso.onView(withId(R.id.registrationVerificationSheetVerifyButton)).perform(click())
        Thread.sleep(3000)
    }

    @When("I click on verify OTP")
    fun iClickOnVerifyOTP() {
        Espresso.onView(withId(R.id.registrationVerificationSheetVerifyButton)).perform(click())
        Thread.sleep(3000)
    }

    @When("I enter the OTP as {string}")
    fun enterAndVerifyPhoneOTP(otp: String) {
        Espresso.onView(withId(R.id.registrationVerificationSheetCodeET))
            .perform(closeSoftKeyboard(), typeText(otp), closeSoftKeyboard())
    }

    @Then("I should see a message stating registration successfully")
    fun verifyRegistrationSuccess() {
        val successText =
            "Your registration is a vital step in realising our vision of universal e-payments. Thank you for joining us on this transformative journey. Please now complete online KYC registration to start"

        Espresso.onView(withId(R.id.registrationSuccessfulActivitySubTextTV)).check(
            matches(
                withText(successText)
            )
        )
    }

    @Then("I should see the verify email address button text changed to {string}")
    fun verifyEmailButton(expectedText: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityEmailVerifiedTV)).check(
            matches(
                withText(expectedText)
            )
        )
    }

    @Then("I select Indian country code")
    fun selectIndianCountryCode() {
        Espresso.onView(withId(R.id.onboardRegistrationActivityCountryCodeSpinner))
            .perform(scrollTo(), click())
        Espresso.onView(withText("+91")).perform(click())
    }

    @Then("I should see the verify phone number button text changed to {string}")
    fun verifyPhoneNumberButton(expectedText: String) {
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneVerifiedTV)).check(
            matches(
                withText(expectedText)
            )
        )
    }
}
