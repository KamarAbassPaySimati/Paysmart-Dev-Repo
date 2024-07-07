package com.afrimax.paymaart

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
        Espresso.onView(ViewMatchers.withId(R.id.primeMembership))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on the Buy button for PrimeX membership")
    fun clickingOnPrimeXMembership() {
        Espresso.onView(ViewMatchers.withId(R.id.primeXMembership))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I select 91 days membership")
    fun iSelectMembership() {
        Espresso.onView(withId(R.id.primeMembership91days))
            .perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @When("I select 30 days membership")
    fun selectMembership() {
        Espresso.onView(withId(R.id.primeMembership30days))
            .perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @When("I select Auto-renewal")
    fun selectAutoRenewal() {
        Espresso.onView(ViewMatchers.withId(R.id.autoRenewal))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on the Submit button for membership selection screen")
    fun clickingOnSubmitButtonForMembershipSelection() {
        Espresso.onView(ViewMatchers.withId(R.id.MembershipSelectionSubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I should be redirected to {string} details screen")
    fun redirectToMembershipDetailsScreen(membership: String) {
        when (membership) {
            "Prime Membership" -> {
                Espresso.onView(ViewMatchers.withId(R.id.PrimeMembership))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }
            "PrimeX Membership" -> {
                Espresso.onView(ViewMatchers.withId(R.id.PrimeXMembership))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }
        }
    }
    @When("I click on the send payment button for membership selection")
    fun clickingOnSendPaymentBForMembershipSelection() {
        Espresso.onView(ViewMatchers.withId(R.id.sendPayment))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on the confirm button for login pin for membership selection")
    fun clickingOnConfirmButtonForLoginPin() {
        Espresso.onView(ViewMatchers.withId(R.id.confirmLoginPin))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see a popup for proceeding membership")
    fun popUpForProceeding() {
        Espresso.onView(ViewMatchers.withId(R.id.proceedingMembershipPopup))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Then("I should be asked for authorisation pin for membership selection")
    fun askingForAuthorisationPin() {
        Espresso.onView(ViewMatchers.withId(R.id.authorisationPin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Then("I should see error message {string} for field with ID {string} for membership selection")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "Login Pin" -> {
                Espresso.onView(withId(R.id.LoginPin)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }
        }
    }
    @When("I enter login PIN {string} for membership selection")
    fun enterLoginPin(pin: String) {
        Espresso.onView(withId(R.id.loginActivityPinET))
            .perform(
                ViewActions.typeText(pin),
                ViewActions.closeSoftKeyboard()
            )
    }
    @When("I should read a message stating {string} for membership selection")
    fun membershipPaymentSuccessMessage(message: String) {
        Espresso.onView(ViewMatchers.withId(R.id.membershipPaymentSuccess))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }


}