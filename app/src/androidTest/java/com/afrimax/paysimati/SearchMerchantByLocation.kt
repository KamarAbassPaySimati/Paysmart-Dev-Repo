package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class SearchMerchantByLocation {

    private var locname = ""

    @When("I click on Location icon")
    fun clickLocationIcon() {
        Espresso.onView(withId(R.id.listMerchantLocation)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I click on Search tab")
    fun clickOnSearch() {
        Espresso.onView(withId(R.id.listMerchantByLocationSearchET)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I enter the location as {string}")
    fun enterLocationName(locationName: String) {
        Espresso.onView(withId(R.id.listMerchantByLocationSearchET))
            .perform(ViewActions.typeText(locationName), ViewActions.closeSoftKeyboard())
        locname = locationName
    }

    @And("I click on Filter")
    fun clickOnFilter() {
        Espresso.onView(withId(R.id.listMerchantFilterBySearch)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I select Hotels & Resorts as Trading type")
    fun selectingRadioButton() {
        Espresso.onView(withText("Hotels & Resorts")).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I click Apply")
    fun clickOnApply() {
        Espresso.onView(withId(R.id.merchantFilterApplyButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should see the Merchants name along with location and Trading types")
    fun viewMerchantByLocation() {
        Espresso.onView(withId(R.id.listMerchantByLocationRV)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Given("I go back to Pay Merchant page")
    fun payMerchantPage() {
        Espresso.onView(withId(R.id.listMerchantByLocationcancelIV)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should read a message stating {string}")
    fun noDataFound(message: String) {
        Espresso.onView(withId(R.id.listMerchantByLocationRV)).check(ViewAssertions.matches(withText(message)))
    }
}