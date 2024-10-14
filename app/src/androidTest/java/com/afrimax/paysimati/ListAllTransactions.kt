package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.afrimax.paymaart.R
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ListAllTransactions {
    @When("I click on drop down for transactions")
    fun iClickOnDropDownTransactions(){
        Espresso.onView(withId(R.id.homeActivityTransactionsTExpandButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see recent four transactions")
    fun iShouldSeeRecentTransactions(){
        Espresso.onView(withId(R.id.homeActivityTransactionsRecyclerView)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on see all link")
    fun iClickOnSeeAll(){
        Espresso.onView(withId(R.id.homeActivityTransactionsSeeAllTV))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to transaction history screen")
    fun iShouldBeRedirectedToHistoryScreen(){
        Espresso.onView(withId(R.id.transactionHistoryActivity)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I search {string}")
    fun iSearch(searchValue :String){
        Espresso.onView(withId(R.id.transactionHistoryActivitySearchET)).perform(
            ViewActions.replaceText(searchValue), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }
    @Then("I should see the list of afrimax transactions")
    fun iShouldAfrimaxTransactions(){
        Espresso.onView(withId(R.id.transactionHistoryActivityRV)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }
    @Then("I should see the searched transactions")
    fun iShouldSearchedTransactions(){
        Espresso.onView(withId(R.id.transactionHistoryActivityRV)).check(matches(isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I should see list of Transactions of last 90 days")
    fun listOfTransaction() {
        onView(withId(R.id.transactionHistoryActivity)).check(
            matches(
                isDisplayed()
            )
        )
    }
}