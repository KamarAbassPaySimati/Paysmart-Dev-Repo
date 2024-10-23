package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewWalletBalance {

    @When("I click on view wallet balance icon")
    fun clickViewIconForWalletBalance() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityEyeButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on view balance button")
    fun clickViewButtonForWalletBalance() {
    //
    }

    @Then("I should be asked for the login pin for viewing wallet balance")
    fun askingForLoginPin() {
        Espresso.onView(ViewMatchers.withId(R.id.viewWalletPinSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see error message {string} for viewing wallet balance")
    fun seeErrorMessage(errorMessage: String) {
        Espresso.onView(ViewMatchers.withId(R.id.viewWalletPinSheetAPF))
            .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
    }

    @When("I enter authorisation PIN {string} for viewing wallet balance")
    fun enterLoginPin(pin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.viewWalletPinSheetAPF)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see the wallet balance")
    fun seeWalletBalance() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityProfileBalanceTV))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}