package com.afrimax.paymaart

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.afrimax.paymaart.ui.splash.SplashScreenActivity
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class SplashScreenDefs(private val scenarioHolder: ActivityScenarioHolder) {

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        scenarioHolder.launch(
            Intent(
                instrumentation.targetContext, SplashScreenActivity::class.java
            )
        )
    }

    @After
    fun finish() {
        //finish
        scenarioHolder.close()
    }

    @Given("The splash screen is displayed")
    fun the_splash_screen_is_displayed() {
        Espresso.onView(ViewMatchers.withId(R.id.splashScreenActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @When("I wait for a few seconds")
    fun i_wait_for_a_few_seconds() {
        Thread.sleep(2000)
    }

    @Then("The main app interface should be displayed")
    fun the_main_app_interface_should_be_displayed() {
        val build = BuildConfig.APP_NAME

        Espresso.onView(ViewMatchers.withId(R.id.mainActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.initialScreenText)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(build)
            )
        )
    }
}