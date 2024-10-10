/*
package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.recyclerview.widget.RecyclerView
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class PayPersonUnregistered {
    @When("I click on pay to person")
    fun iClickOnPayPerson(){
        Espresso.onView(withId(R.id.payPersonButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to pay to person screen")
    fun iShouldBeRedirectedToPayPerson(){
        Espresso.onView(withId(R.id.payPersonScreen)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I should see the list of customers")
    fun iShouldListOfCustomers(){
        Espresso.onView(withId(R.id.listOfCustomers)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I click on the first profile of customer")
    fun iClickFirstProfileOfCustomer(){
        Espresso.onView(withId(R.id.listOfCustomers))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))
    }
    @Then("I should be redirected enter details of unregistered user")
    fun iShouldBeRedirectedToEnterDetails(){
        Espresso.onView(withId(R.id.enterDetailsScreen)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

}*/
