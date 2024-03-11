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
class InitialStepDefs(private val scenarioHolder: ActivityScenarioHolder) {

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        scenarioHolder.launch(Intent(instrumentation.targetContext, MainActivity::class.java))
    }

    @After
    fun finish() {
        //finish
        scenarioHolder.close()
    }

    @Given("I have the Initial screen")
    fun i_have_the_initial_screen() {

    }

    @Then("I should see respective welcome message")
    fun i_should_see_respective_welcome_message() {
        val build = BuildConfig.APP_NAME
        Espresso.onView(ViewMatchers.withId(R.id.initialScreenText)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(build)
            )
        )
    }
}