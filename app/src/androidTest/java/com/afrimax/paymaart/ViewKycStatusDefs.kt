package com.afrimax.paymaart

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewKycStatusDefs {

    @When("I open the menu, I should view the KYC status as {string} and {string}")
    fun openMenuAndCheckKYC(kycType: String, kycStatus: String) {
        Espresso.onView(withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)

        Espresso.onView(withId(R.id.homeDrawerKycTypeTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(kycType)))
        Espresso.onView(withId(R.id.homeDrawerKycStatusTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(kycStatus)))
    }

}