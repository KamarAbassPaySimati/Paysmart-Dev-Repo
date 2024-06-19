package com.afrimax.paymaart

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
class CustomerViewProfile {

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

    @When("I select back button")
    fun iSelectBackButton() {
        Espresso.onView(withId(R.id.kycProgressActivityBackButton2IV)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to home screen")
    fun redirectedToHomeScreen() {
        Espresso.onView(withId(R.id.homeActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I should view my name and paymaart ID starting with CMR")
    fun iShouldSeeMyNameAndPayMaartIdStartingWithCMR() {
        val userName = getText(Espresso.onView(withId(R.id.ProfileName)))
        val paymaartID = getText(Espresso.onView(withId(R.id.ProfilePaymaart)))

        require(userName.isNotEmpty())
        require(paymaartID.startsWith("CMR"))
    }
}

}