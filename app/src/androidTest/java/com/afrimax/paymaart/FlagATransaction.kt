package com.afrimax.paymaart

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import net.datafaker.Faker
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class FlagATransaction {
    @When("I click on flag transaction icon")
    fun iClickOnFlagTransactionIcon(){
        Espresso.onView(withId(R.id.flagTransactionIcon))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on flag transaction button")
    fun iClickOnFlagTransactionButton(){
        Espresso.onView(withId(R.id.flagTransactionButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to select reasons screen")
    fun iShouldBeRedirectedToSelectReason(){
        Espresso.onView(withId(R.id.flagTransactionScreen)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on first reason for flag transaction")
    fun iClickOnAnyOneReason(){
        Espresso.onView(withId(R.id.flagTransactionreasonOne))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see a flag transaction success pop up")
    fun iShouldSeeFlagTransaction(){
        Espresso.onView(withId(R.id.successFlagTransactionPopUp)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on done button for flag transaction")
    fun iClickOnDoneButton(){
        Espresso.onView(withId(R.id.DoneButtonForPopUp))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see flag transaction button disabled")
    fun iShouldSeeFlagTransactionButtonDisabled(){
        Espresso.onView(withId(R.id.flagTransactionButton))
            .check(matches(ViewMatchers.isEnabled()))
    }
}