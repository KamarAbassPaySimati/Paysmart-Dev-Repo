package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class FlagATransaction {
    @When("I click on flag transaction icon")
    fun iClickOnFlagTransactionIcon(){
        Espresso.onView(withId(R.id.paymentSuccessfulFlagPayment))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on flag transaction button")
    fun iClickOnFlagTransactionButton(){
        Espresso.onView(withId(R.id.flagTransactionActivityFlagButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to select reasons screen")
    fun iShouldBeRedirectedToSelectReason(){
        Espresso.onView(withId(R.id.flagTransactionActivity)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on first reason for flag transaction")
    fun iClickOnAnyOneReason(){
        Espresso.onView(withId(R.id.flagTransactionActivityReason1Title))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see a flag transaction success pop up")
    fun iShouldSeeFlagTransaction(){
        Espresso.onView(withId(R.id.flaggingSuccessSheet)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on done button for flag transaction")
    fun iClickOnDoneButton(){
        Espresso.onView(withId(R.id.flaggingSuccessSheetDoneButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see flag transaction button disabled")
    fun iShouldSeeFlagTransactionButtonDisabled(){
        Espresso.onView(withId(R.id.flagTransactionActivityFlagButton))
            .check(matches(ViewMatchers.isNotEnabled()))
    }
}