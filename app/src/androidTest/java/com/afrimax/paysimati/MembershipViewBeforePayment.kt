package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MembershipViewBeforePayment {
    @When("I click on pay to paymaart")
    fun iClickOnPayToPaymaart() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityPayPaymaartButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }
    @When("I click the back button in membership banner screen")
    fun iClickBackButton() {
        Espresso.onView(ViewMatchers.withId(R.id.membershipPlansBackButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

}