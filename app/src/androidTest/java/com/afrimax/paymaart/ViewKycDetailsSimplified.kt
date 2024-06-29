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
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewKycDetailsSimplified {

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


    @Then("I should see monthly withdrawal and monthly income as Up to 300,000.00 MWK")
    fun monthlyTransaction() {
        val text1 = getText(Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoMonthlyWithdrawalTV)))
        require(text1 == "Up to 300,000.00 MWK")
        val text2 = getText(Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoMonthlyIncomeTV)))
        require(text2 == "Up to 300,000.00 MWK")
    }

    @When("I click on submit button for Employer Letter")
    fun submitEmployerLetter() {
        Espresso.onView(ViewMatchers.withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }
    @Given("I am in home screen")
    fun iAmInHomeScreen( ){
        Espresso.onView(ViewMatchers.withId(R.id.homeActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}