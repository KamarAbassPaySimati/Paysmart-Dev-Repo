package com.afrimax.paymaart

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MembershipBannerViewDefs {

    @OptIn(DelicateCoroutinesApi::class)
    @Then("I make the user approved")
    fun iMakeUserApproved() {
        GlobalScope.launch {
            makeUserApproved()
        }
        Thread.sleep(7000)
    }

    @When("I click on the finish button")
    fun clickOnFinishButton() {
        Espresso.onView(ViewMatchers.withId(R.id.kycFinishButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should read {string} above the membership banner")
    fun membershipBanner(message: String) {
        Espresso.onView(ViewMatchers.withId(R.id.membershipBanner))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

}