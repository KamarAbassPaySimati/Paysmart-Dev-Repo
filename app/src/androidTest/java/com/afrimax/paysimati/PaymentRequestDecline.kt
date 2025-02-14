package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class PaymentRequestDecline {
    @When("I see the payment request from the merchant")
    fun viewPaymentRequest() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @And("I click on Decline button")
    fun clickDeclineButton() {
        Espresso.onView(ViewMatchers.withContentDescription("declineButton")).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should see the status of payment request as declined")
    fun viewDeclinedStatus() {
        Espresso.onView(ViewMatchers.withContentDescription("declinedStatus"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}


