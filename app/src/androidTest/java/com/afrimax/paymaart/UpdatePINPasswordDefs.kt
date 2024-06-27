package com.afrimax.paymaart

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class UpdatePINPasswordDefs {

    @When("I click the back button in kyc progress screen")
    fun iClickBackButton() {
        Espresso.onView(ViewMatchers.withId(R.id.kycProgressActivityBackButton2IV))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I open menu and navigate to the update PIN or password")
    fun openUpdatePinOrPassword() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.homeDrawerSettingsTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.homeDrawerUpdatePasswordContainer))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I enter user PIN {string} for PIN or Password")
    fun enterUserPin(pin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityCurrentPasswordET))
            .perform(
                ViewActions.scrollTo(), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
            )
    }

    @When("I click on update button for PIN or Password")
    fun clickUpdateButton() {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityUpdateButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I choose password section for update")
    fun iChoosePasswordSection() {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityUpdateTypePasswordRB))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I should see error message {string} for field with ID {string} for updating PIN or Password")
    fun checkErrorMessageDisplayed(errorMessage: String, fieldID: String) {
        when (fieldID) {
            "Current PIN/Password" -> {
                Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityCurrentPasswordWarningTV))
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "New PIN" -> {
                Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityNewPinWarningTV))
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "New Password" -> {
                Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityNewPasswordWarningTV))
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }
        }
    }

    @When("I enter the new PIN as {string} for updating PIN")
    fun enterNewPin(newPin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityNewPinET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(newPin),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the confirm new PIN as {string} for updating PIN")
    fun enterConfirmPin(confirmPIN: String) {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityConfirmPinET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(confirmPIN),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the new password as {string} for updating password")
    fun enterNewPassword(newPassword: String) {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityNewPasswordET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(newPassword),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the confirm new password as {string} for updating password")
    fun enterConfirmPassword(confirmPassword: String) {
        Espresso.onView(ViewMatchers.withId(R.id.updatePasswordPinActivityConfirmPasswordET))
            .perform(
                ViewActions.replaceText(""),
                ViewActions.typeText(confirmPassword),
                ViewActions.closeSoftKeyboard()
            )
    }

    @When("I should read a message stating {string}")
    fun pinPasswordUpdationSuccessfully(message: String) {
        Espresso.onView(ViewMatchers.withId(R.id.sheetPinPasswordChangeTitleTV))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }


}