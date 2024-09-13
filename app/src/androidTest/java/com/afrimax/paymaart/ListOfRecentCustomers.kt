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
class ListOfRecentCustomers {
    @When("I click on cancel button")
    fun iClickOnCancelButton(){
       /* Espresso.onView(withId(R.id.cancelButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)*/
    }
    @Then("I should see the recently transacted customer transaction")
    fun iShouldListOfRecentCustomers(){
      /*  Espresso.onView(withId(R.id.recentListOfCustomers)).check(matches(isDisplayed()))
        Thread.sleep(5000)*/
    }
}