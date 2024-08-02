package com.afrimax.paymaart
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
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
    @When("I open menu and click on refund payment button")
    fun openMenuAndClickRefundRequest() {
        onView(withId(R.id.homeActivityMenuIcon)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.homeDrawerRefundRequestTV)).perform(ViewActions.scrollTo(), click())
        Thread.sleep(3000)
    }
    @Then("I should be redirected to refund request screen")
    fun iShouldBeRedirectedToRefundRequest(){
        Espresso.onView(withId(R.id.refundRequestActivity)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I click on sort button")
    fun iClickOnSortButton(){
        Espresso.onView(withId(R.id.refundRequestActivitySortButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see sort option pop up")
    fun iShouldSeeSortPopUp(){
        Espresso.onView(withId(R.id.sortParameterBottomSheet)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the sort option as {string}")
    fun iSelectSortAs(filter: String){
        when(filter){
            "today"-> {
                Espresso.onView(withId(0)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "yesterday"-> {
                Espresso.onView(withId(1)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 7 day"-> {
                Espresso.onView(withId(2)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 30 day"-> {
                Espresso.onView(withId(3)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "last 60 day"-> {
                Espresso.onView(withId(4)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @When("I click on filter button")
    fun iClickOnFilterButton(){
        Espresso.onView(withId(R.id.refundRequestActivityFilterButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see the filter pop up")
    fun iShouldSeeTheFilterPopUp(){
        Espresso.onView(withId(R.id.filterParameterBottomSheet)).check(matches(isDisplayed()))
        Thread.sleep(3000)
    }
    @When("I select the filter as {string}")
    fun iSelectFilterAs(filter: String){
        when(filter){
            "refunded"-> {
                Espresso.onView(withId(R.id.filterParameterRefundedCheckbox)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "pending"-> {
                Espresso.onView(withId(R.id.filterParameterPendingCheckbox)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
            "rejected"-> {
                Espresso.onView(withId(R.id.filterParameterRejectedCheckbox)).perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }
    @Then("I should see the transactions with filter {string}")
    fun iShouldSeeTransactions(filter: String){
        Espresso.onView(withId(R.id.refundRequestRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(ViewMatchers.hasDescendant(withText(filter))))
    }

    @When("I click on apply button")
    fun i_click_on_apply_button( ) {
        onView(withId(R.id.filterParameterApplyButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @When("I click on clear all button")
    fun iClickOnClearAllButton(){
        Espresso.onView(withId(R.id.filterParameterClearButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
}