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
class ViewListOfMerchantTransaction {

    @And("I click back button on Membership plans page")
    fun clickBackButton() {

        Thread.sleep(7000)
        Espresso.onView(withId(R.id.membershipPlansBackButton)).perform(ViewActions.click())
        Thread.sleep(5000)

    }
    @When("I click on the Merchant option")
    fun clickMerchantOption() {

        Espresso.onView(withId(R.id.homeActivityPayMerchantButton)).perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should see a list of merchants I have transacted with in the last 90 days")
    fun viewMerchantList() {
        Espresso.onView(withId(R.id.listMerchantTransactionActivity)).check(
            ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
}