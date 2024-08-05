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
class ListAllTransactions {
    @When("I click on drop down for transactions")
    fun iClickOnDropDownTransactions(){
        Espresso.onView(withId(R.id.transactionsDropDown))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see recent four transactions")
    fun iShouldSeeRecentTransactions(){
        Espresso.onView(withId(R.id.recent4Transactions)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on see all link")
    fun iClickOnSeeAll(){
        Espresso.onView(withId(R.id.seeAllLink))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to transaction history screen")
    fun iShouldBeRedirectedToHistoryScreen(){
        Espresso.onView(withId(R.id.transactionsList)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I search {string}")
    fun iSearch(searchValue :String){
        Espresso.onView(withId(R.id.searchBar)).perform(
            ViewActions.replaceText(searchValue), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }
    @Then("I should see the list of afrimax transactions")
    fun iShouldAfrimaxTransactions(){
        Espresso.onView(withId(R.id.transactionsList)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I should see the searched transactions")
    fun iShouldSearchedTransactions(){
        Espresso.onView(withId(R.id.transactionsList)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
}