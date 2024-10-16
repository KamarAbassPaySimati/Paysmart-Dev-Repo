package com.afrimax.paysimati

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.afrimax.paysimati.ui.utils.adapters.ChoosePlanAdapter
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class PayAfrimax {

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

    @When("I click on pay to afrimax")
    fun clickingOnPayToAfrimax() {
        Espresso.onView(withId(R.id.homeActivityPayAfrimaxButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click the continue button for pay to afrimax")
    fun continueButtonScreenOne() {
        Espresso.onView(withId(R.id.validateAfrimaxIdActivityContinueButton))
            .perform(ViewActions.click())
        Thread.sleep(6000)
    }

    @When("I click the continue button for pay to afrimax screen 2")
    fun continueButtonScreenTwo() {
        Espresso.onView(withId(R.id.validateAfrimaxIdActivityContinuePayButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should see error message {string} for field with ID {string} for pay to afrimax")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "Afrimax ID" -> {
                Espresso.onView(withId(R.id.validateAfrimaxIdActivityAfrimaxIdWarningTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "Amount" -> {
                Espresso.onView(withId(R.id.payAfrimaxActivityPaymentErrorTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "Login Pin" -> {
                Espresso.onView(withId(R.id.sendPaymentSheetAPF)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }
        }
    }

    @When("I enter afrimax ID {string}")
    fun enterAfrimaxId(afrimaxId: String) {
        Espresso.onView(withId(R.id.validateAfrimaxIdActivityAfrimaxIdET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(afrimaxId),
            ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see prefilled field for Afrimax name")
    fun afrimaxNamePrefilled() {
        val afrimaxName =
            getText(Espresso.onView(withId(R.id.validateAfrimaxIdActivityAfrimaxNameTV)))
        require(afrimaxName.isNotEmpty())
    }

    @When("I click on the Re-enter button for Afrimax ID")
    fun clickOnReEnterAfrimaxID() {
        Espresso.onView(withId(R.id.validateAfrimaxIdActivityReEnterButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to send payment screen")
    fun redirectingToSendPaymentScreen() {
        Espresso.onView(withId(R.id.payAfrimaxActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on Send Payment button for pay to afrimax")
    fun clickingOnSendPaymentButton() {
        Espresso.onView(withId(R.id.payAfrimaxActivitySendPaymentButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I enter amount as {string} for pay to afrimax")
    fun enterAmount(amount: String) {
        Espresso.onView(withId(R.id.payAfrimaxActivityAmountET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(amount),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter login PIN {string} for payment")
    fun enterLoginPin(pin: String) {
        Espresso.onView(withId(R.id.sendPaymentSheetAPF)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see a popup for proceeding payment for pay to afrimax")
    fun popupForProceedingPayment() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should be asked for the login pin for payment")
    fun askingForLoginPin() {
        Espresso.onView(withId(R.id.sendPaymentSheetAPF))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on proceed button for payment")
    fun clickingOnProceedButtonForPayment() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on the confirm button for login pin")
    fun clickingOnConfirmButtonForLogin() {
        Espresso.onView(withId(R.id.sendPaymentConfirmButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I should read a message stating {string} for payment")
    fun paymentSuccessful(message: String) {
        Espresso.onView(withId(R.id.paymentSuccessfulStatusText))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    @When("I select Choose Plan tab")
    fun selectingCustomerTab() {
        Espresso.onView(withId(R.id.payAfrimaxActivityChoosePlanTV))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I click on first plan in list")
    fun clickingOnFirstPlan() {
        Espresso.onView(withId(R.id.payAfrimaxActivityRV)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ChoosePlanAdapter.ChoosePlanViewHolder>(
                0, ViewActions.click()
            )
        )
    }

}