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
class ListAllTransactionsFilter {
    @When("I click on drop down for transaction")
    fun iClickOnDropDownTransactions(){
        Espresso.onView(withId(R.id.homeActivityTransactionsTExpandButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see recent four transactions")
    fun iShouldSeeRecentFourTransactions(){
        Espresso.onView(withId(R.id.homeActivityTransactionsRecyclerView)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on see all link")
    fun iClickOnSeeAll(){
        Espresso.onView(withId(R.id.homeActivityTransactionsSeeAllTV)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to transaction history screen")
    fun iShouldBeRedirectedToTransactionHistory(){
        Espresso.onView(withId(R.id.transactionHistoryActivity)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on filter button")
    fun iClickOnFilter(){
        Espresso.onView(withId(R.id.transactionHistoryActivityFilterIV)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see the filter pop up")
    fun iShouldSeeFilterPopUp(){
        Espresso.onView(withId(R.id.transactionHistorySheet)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the filter type as {string}")
    fun iSelectTheFilterType(filterType: String){
        when(filterType){
            "timePeriod"->{
                Espresso.onView(withId(R.id.transactionHistorySheetTimePeriodTV)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "transactionType"-> {
                Espresso.onView(withId(R.id.transactionHistorySheetTransactionTypeTV)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I select the filter as {string}")
    fun iSelectTheFilter(filterType: String){
        when(filterType){
            "today"->{
                Espresso.onView(withId(R.id.transactionHistorySheetTodayRB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "yesterday"-> {
                Espresso.onView(withId(R.id.transactionHistorySheetYesterdayRB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 7 days"->{
                Espresso.onView(withId(R.id.transactionHistorySheetLast7RB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 30 days"-> {
                Espresso.onView(withId(R.id.transactionHistorySheetLast30RB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "cash-in"->{
                Espresso.onView(withId(R.id.transactionHistorySheetCashInCB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "cash-out"-> {
                Espresso.onView(withId(R.id.transactionHistorySheetCashOutCB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "Pay Afrimax"->{
                Espresso.onView(withId(R.id.transactionHistorySheetPayAfrimaxCB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "Pay Person"-> {
                Espresso.onView(withId(R.id.transactionHistorySheetPayPersonCB)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I click on apply button")
    fun iClickApplyButton(){
        Espresso.onView(withId(R.id.transactionHistorySheetApplyButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on clear all button")
    fun iClickClearAllButton(){
        Espresso.onView(withId(R.id.transactionHistorySheetClearAllButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
}