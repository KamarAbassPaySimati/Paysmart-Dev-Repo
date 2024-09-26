package com.afrimax.paysimati

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class CustomerCompletePendingKyc {

    private fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }

    @When("I click on payment option")
    fun clickOnPaymentOptions() {
        Espresso.onView(withId(R.id.homeActivityEyeButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should see a popup for complete pending KYC")
    fun iShouldSeeTheCompletePendingKYC() {
        Espresso.onView(withId(R.id.sheetCompleteKyc))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val text = getText(Espresso.onView(withId(R.id.sheetCompleteKycSubTextTV)))
        require(text == "Complete this now for full access to Paymaart services")
    }

    @When("I click on complete pending KYC registration")
    fun clickOnCompletePendingKycRegistration() {
        Espresso.onView(withId(R.id.sheetCompleteKycCompleteKycButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

}