package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class RecentMerchantTransactedList {

    private var name = ""

    @When("I click on drop down for Merchants")
    fun clickMerchantDropDown() {
        Espresso.onView(withId(R.id.homeActivityMerchantsTExpandButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should see recent four Merchants transactions")
    fun recentMerchTrans() {
        Espresso.onView(withId(R.id.homeActivityMerchantsRecyclerView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on see all")
    fun clickSeeAll() {
        Espresso.onView(withId(R.id.homeActivityMerchantsSeeAllTV)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should be redirected to recently transacted merchants in last 90 days screen")
    fun last90DaysScreen() {
        Espresso.onView(withId(R.id.listMerchantTransactionRV)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I search for {string}")
    fun searchTab(merchantName: String) {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET))
            .perform(ViewActions.typeText(merchantName), ViewActions.closeSoftKeyboard())
        name = merchantName
    }

    @Then("I should read a message stating {string} in pay Merchant screen")
    fun readMessage(displayScreenMessage: String) {
        Espresso.onView(withId(R.id.listMerchantByLocationRV)).check(ViewAssertions.matches(withText(displayScreenMessage)))
        Thread.sleep(5000)
    }

    @Then("I should see the list of Merchants")
    fun listOfSearchedMerchant() {
        Espresso.onView(withId(R.id.listMerchantTransactionRV)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
}