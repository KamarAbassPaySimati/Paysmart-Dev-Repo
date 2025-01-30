package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class SearchMerchant {

    private var username = ""

    @When("I click on the Search tab")
    fun clickSearchTab() {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I enter trading name as {string}")
    fun enterTradingName(tradingName: String) {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET))
            .perform(ViewActions.typeText(tradingName), ViewActions.closeSoftKeyboard())
        username = tradingName
    }

    @Then("I should see the Trading name along with Paymaart ID")
    fun payMerchantScreen() {
        Espresso.onView(withId(R.id.listMerchantTransactionActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on clear button on search tab")
    fun clickClearButton() {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchClearIV)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("the search tab should be empty")
    fun searchTab() {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

}