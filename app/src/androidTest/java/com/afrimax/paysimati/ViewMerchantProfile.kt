package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewMerchantProfile {
    @Given("I am on the chat screen")
    fun chatScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @When("I click on the Merchant name")
    fun clickMerchantName() {
        Espresso.onView(ViewMatchers.withContentDescription("MerchantProfile")).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should be able to see the Merchant profile with details")
    fun profileScreen() {
        Espresso.onView(withId(R.id.viewMerchantActivityDetails)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

}