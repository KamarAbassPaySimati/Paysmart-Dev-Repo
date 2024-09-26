package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewTransaction {
    @Then("I click on the first row of the transaction table")
    fun iClickFirstRowOfTransactions(){
        Espresso.onView(withId(R.id.transactionHistoryActivityRV))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))
    }
    @Then("I should be redirected to payment receipt screen")
    fun iShouldBeRedirectedPaymentReceiptScreen(){
        Espresso.onView(withId(R.id.paymentReceiptActivity)).check(matches(isDisplayed()))
    }
}