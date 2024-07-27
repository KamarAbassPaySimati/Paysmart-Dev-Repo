package com.afrimax.paymaart
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class RefundPaymentStatus {
    @When("I click on refund request option")
    fun iClickOnRefundRequests(){
        Espresso.onView(withId(R.id.refundRequestOption)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to refund request screen")
    fun iShouldBeRedirectedToRefundRequest(){
        Espresso.onView(withId(R.id.refundRequestScreen)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on sort button")
    fun iClickOnSortButton(){
        Espresso.onView(withId(R.id.sortButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see sort option pop up")
    fun iShouldSeeSortPopUp(){
        Espresso.onView(withId(R.id.sortPopup)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the sort option as {string}")
    fun iSelectSortAs(filter: String){
        when(filter){
            "today"-> {
                Espresso.onView(withId(R.id.sortToday)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "yesterday"-> {
                Espresso.onView(withId(R.id.sortYesterday)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 7 day"-> {
                Espresso.onView(withId(R.id.sort7days)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 30 day"-> {
                Espresso.onView(withId(R.id.sort30days)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 60 day"-> {
                Espresso.onView(withId(R.id.sort60days)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I click on filter button")
    fun iClickOnFilterButton(){
        Espresso.onView(withId(R.id.filterButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see the filter pop up")
    fun iShouldSeeTheFilterPopUp(){
        Espresso.onView(withId(R.id.filterPopup)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the filter as {string}")
    fun iSelectFilterAs(filter: String){
        when(filter){
            "refunded"-> {
                Espresso.onView(withId(R.id.refundedFilter)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "pending"-> {
                Espresso.onView(withId(R.id.pendingFilter)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "rejected"-> {
                Espresso.onView(withId(R.id.rejectedFilter)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @Then("I should see the transactions with filter {string}")
    fun iShouldSeeTransactions(filter: String){
        Espresso.onView(withId(R.id.refundTransactions))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(ViewMatchers.hasDescendant(withText(filter))))
    }
    @When("I click on clear all button")
    fun iClickOnClearAllButton(){
        Espresso.onView(withId(R.id.clearAll)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
}