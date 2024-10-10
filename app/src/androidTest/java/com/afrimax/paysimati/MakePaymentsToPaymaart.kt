package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MakePaymentsToPaymaart {

    @When("I click on the Buy button for Prime membership")
    fun clickingOnPrimeMembership() {
        Espresso.onView(withId(R.id.buy_button_prime))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on the Buy button for PrimeX membership")
    fun clickingOnPrimeXMembership() {
        Espresso.onView(withId(R.id.buy_button_prime_x))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I select 91 days membership")
    fun iSelectMembership() {
        Espresso.onView(withId(1))
            .perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @When("I select 30 days membership")
    fun selectMembership() {
        Espresso.onView(withId(0))
            .perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @When("I select Auto-renewal")
    fun selectAutoRenewal() {
        Espresso.onView(withId(R.id.membershipPlansAutoRenewalSwitch))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on the Submit button for membership selection screen")
    fun clickingOnSubmitButtonForMembershipSelection() {
        Espresso.onView(withId(R.id.membershipPlansSubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should click on proceed button")
    fun clickOnProceedButton() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed))
            .perform(ViewActions.click())
        Thread.sleep(2000)
    }

    @When("I should be redirected to {string} details screen")
    fun redirectToMembershipDetailsScreen(membership: String) {
        when (membership) {
            "Prime Membership" -> {
                Espresso.onView(withId(R.id.purchasedMembershipPlanMembershipType))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }
            "PrimeX Membership" -> {
                Espresso.onView(withId(R.id.purchasedMembershipPlanMembershipType))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }
        }
    }
    @When("I click on the send payment button for membership selection")
    fun clickingOnSendPaymentBForMembershipSelection() {
        Espresso.onView(withId(R.id.purchasedMembershipPlansSubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on the confirm button for login pin for membership selection")
    fun clickingOnConfirmButtonForLoginPin() {
        Espresso.onView(withId(R.id.sendPaymentConfirm))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see a popup for proceeding membership")
    fun popUpForProceeding() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Then("I should be asked for authorisation pin for membership selection")
    fun askingForAuthorisationPin() {
        Espresso.onView(withId(R.id.sendPaymentPin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Then("I should see error message {string} for field with ID {string} for membership selection")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "Login Pin" -> {
                Espresso.onView(withId(R.id.sendPaymentPinETWarning)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }
        }
    }
    @When("I enter login PIN {string} for membership selection")
    fun enterLoginPin(pin: String) {
        Espresso.onView(withId(R.id.sendPaymentPin))
            .perform(
                ViewActions.typeText(pin),
                ViewActions.closeSoftKeyboard()
            )
    }
    @When("I should read a message stating {string} for membership selection")
    fun membershipPaymentSuccessMessage(message: String) {
        Espresso.onView(withId(R.id.paymentSuccessfulStatusText))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    @Then("I should be redirected to payment successful screen")
    fun redirectedToMembershipPlanScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.paymentSuccessfulActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(2000)
    }
}