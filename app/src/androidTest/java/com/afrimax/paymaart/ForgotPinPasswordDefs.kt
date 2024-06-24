package com.afrimax.paymaart

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ForgotPinPasswordDefs {

    private fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }

    @When("I click on forgot PIN")
    fun iClickForgotPin() {
        Espresso.onView(withId(R.id.ForgotPinTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to forgot password pin screen")
    fun forgotPinPasswordEmailScreen() {
        Espresso.onView(withId(R.id.forgotPasswordPin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @When("I enter my email address as {string}")
    fun enterEmailAddress(email: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinEmail)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(email), ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I click on proceed button for forgot password or pin")
    fun iClickProceedButtonForForgotPasswordPin() {
        Espresso.onView(withId(R.id.forgotPasswordPinProceedButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should be redirected to forgot password or pin OTP screen")
    fun forgotPasswordPinOTPScreen() {
        Espresso.onView(withId(R.id.forgotPasswordPinOtpView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @When("I enter the forgot OTP as {string}")
    fun enterOTP(otp: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinOtp)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(otp), ViewActions.closeSoftKeyboard()
        )
    }

    @And("I click on verify button for forgot password pin")
    fun iClickVerifyButtonForForgotPasswordPin() {
        Espresso.onView(withId(R.id.forgotPasswordOtpVerifyButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I should see error message {string} for field with ID {string} for forgot")
    fun checkErrorMessageDisplayedForForgot(errorMessage: String, fieldID: String) {
        when (fieldID) {
            "Forgot Password OTP Field" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinOtpWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }
        }
    }

    @Then("I should be redirected to confirm forgot pin screen")
    fun updatePinScreen() {
        Espresso.onView(withId(R.id.forgotPasswordPinSetPinView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I enter the new PIN as {string}")
    fun enterNewPin(string: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinNewPin)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(string), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the confirm new PIN as {string}")
    fun enterConfirmNewPin(pin: String) {
        Espresso.onView(withId(R.id.forgotPasswordConfirmPin)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
        )
    }

    @And("I enter the security answer as {string} for pin")
    fun enterSecurityQuestionAnswerForPin(securityAnswers: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinPinSecurityQuestion)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(securityAnswers), ViewActions.closeSoftKeyboard()
        )
    }

    @And("I enter the security answer as {string} for password")
    fun enterSecurityQuestionAnswerForPassword(securityAnswers: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinPasswordSecurityQuestion)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(securityAnswers), ViewActions.closeSoftKeyboard()
        )
    }

    @And("I click on reset button for forgot pin")
    fun iClickResetButtonForForgotPin() {
        Espresso.onView(withId(R.id.forgotPasswordPinPinResetButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should see error message {string} for field with ID {string} for forgot pin")
    fun checkErrorMessageIsDisplayedForPin(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "New PIN" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinNewPinWarning)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "Security Answer" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinPinSecurityQuestionWarning)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }
        }
    }

    @Then("I should view text PIN updated successfully")
    fun viewSuccessMessageForPINUpdate() {
        val text = getText(Espresso.onView(withId(R.id.forgotPasswordPinUpdatedSubText)))
        require(text == "Your PIN has been successfully changed")
    }

    @When("I select login with password")
    fun iSelectLoginWithPassword() {
        Espresso.onView(withId(R.id.loginPasswordButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on forgot password")
    fun iClickForgotPassword() {
        Espresso.onView(withId(R.id.loginForgotPassword))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to confirm forgot Password screen")
    fun confirmPasswordScreen() {
        Espresso.onView(withId(R.id.forgotPasswordPinSetPasswordView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @And("I enter the new password as {string}")
    fun enterNewPassword(string: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinNewPassword)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(string), ViewActions.closeSoftKeyboard()
        )
    }

    @And("I enter the confirm new password as {string}")
    fun enterConfirmNewPassword(string: String) {
        Espresso.onView(withId(R.id.forgotPasswordPinConfirmPassword)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(string), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click on reset button for forgot password")
    fun iClickResetForPassword() {
        Espresso.onView(withId(R.id.forgotPasswordPinPasswordResetButton)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @Then("I should see error message {string} for field with ID {string} for forgot password")
    fun checkErrorMessageIsDisplayedForPassword(errorMessage: String, fieldID: String) {
        when (fieldID) {
            "New Password" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinNewPasswordWarning)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "Confirm Password" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinConfirmPasswordWarning)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "Security Answer" -> {
                Espresso.onView(withId(R.id.forgotPasswordPinPasswordSecurityQuestionWarning))
                    .check(
                        ViewAssertions.matches(
                            ViewMatchers.withText(errorMessage)
                        )
                )
            }
        }
    }

    @Then("I should view text Password updated successfully")
    fun viewSuccessMessageForPasswordUpdate() {
        val text = getText(Espresso.onView(withId(R.id.forgotPasswordPinUpdatedSubText)))
        require(text == "Your password has been successfully changed")
    }

}