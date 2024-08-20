package com.afrimax.paymaart

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
class RecentTransactedCustomerList {
    @When("I click on drop down for persons")
    fun iClickOnDropDownTransactions(){
        Espresso.onView(withId(R.id.personsDropDown))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see recent four persons")
    fun iShouldSeeRecentTransactions(){
        Espresso.onView(withId(R.id.recent4Persons)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on see all link")
    fun iClickOnSeeAll(){
        Espresso.onView(withId(R.id.seeAllLink))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to recent transacted customer screen")
    fun iShouldBeRedirectedToHistoryScreen(){
        Espresso.onView(withId(R.id.personsList)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I should see the list of customers")
    fun iShouldListCustomers(){
        Espresso.onView(withId(R.id.personsList)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I should read a message stating {string}")
    fun iShouldReadAMessage(errorMessage : String){
        Espresso.onView(withId(R.id.actualMessage)).check(matches(ViewMatchers.withText(errorMessage)))
    }
}