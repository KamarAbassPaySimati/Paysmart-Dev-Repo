package com.afrimax.paymaart

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class SplashScreenDefs(private val scenarioHolder: ActivityScenarioHolder) {

    @Before
    fun setup() {
        
    }

    @After
    fun finish() {
        //finish
    }

    @Given("The splash screen is displayed")
    fun the_splash_screen_is_displayed() {
       val build = BuildConfig.APP_NAME
       Espresso.onView(ViewMatchers.withId(R.id.splashScreenId)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @And("I wait for a few seconds")
    fun i_wait_for_a_few_seconds() {
        Thread.sleep(100)
    }

    @Then("The main app interface should be displayed")
    fun the_main_app_interface_should_be_displayed() {
       Espresso.onView(ViewMatchers.withId(R.id.mainInterfaceId)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.loginTextId)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Login")
            )
        )
    }
}