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
class ListAllTransactionsFilter {
    @When("I click on drop down for transaction")
    fun iClickOnDropDownTransactions(){
        Espresso.onView(R.id.dropDownTransactionsButton).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see recent four transactions")
    fun iShouldSeeRecentFourTransactions(){
        Espresso.onView(R.id.recentFourTransactionsView).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on see all link")
    fun iClickOnSeeAll(){
        Espresso.onView(R.id.seeAllLinkText).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to transaction history screen")
    fun iShouldBeRedirectedToTransactionHistory(){
        Espresso.onView(R.id.transactionHistoryScreen).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on filter button")
    fun iClickOnFilter(){
        Espresso.onView(R.id.filterOption).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see the filter pop up")
    fun iShouldSeeFilterPopUp(){
        Espresso.onView(R.id.filterPopUp).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the filter type as {string}")
    fun iSelectTheFilterType(filterType: String){
        when(filterType){
            "timePeriod"->{
                Espresso.onView(R.id.timePeriodFilter).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "transactionType"-> {
                Espresso.onView(R.id.transactionTypeFilter).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I select the filter as {string}")
    fun iSelectTheFilter(filterType: String){
        when(filterType){
            "today"->{
                Espresso.onView(R.id.today).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "yesterday"-> {
                Espresso.onView(R.id.yesterday).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 7 days"->{
                Espresso.onView(R.id.last7Days).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 30 days"-> {
                Espresso.onView(R.id.last30Days).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "cash-in"->{
                Espresso.onView(R.id.cashIn).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "cash-out"-> {
                Espresso.onView(R.id.cashOut).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "Pay Afrimax"->{
                Espresso.onView(R.id.payAfrimax).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "Pay Person"-> {
                Espresso.onView(R.id.payPerson).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I click on apply button")
    fun iClickApplyButton(){
        Espresso.onView(R.id.applyButton).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on clear all button")
    fun iClickClearAllButton(){
        Espresso.onView(R.id.clearAllButton).perform(ViewActions.click())
        Thread.sleep(3000)
    }
}