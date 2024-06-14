package com.afrimax.paymaart.agent

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith


@RunWith(Cucumber::class)
class LogoutDefs {

    @When("I open menu and click on logout button")
    fun openMenuAndClickLogout() {
        onView(withId(R.id.homeActivityMenuIcon)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.homeDrawerSettingsTV)).perform(ViewActions.scrollTo(), click())
        onView(withId(R.id.homeDrawerLogOutContainer)).perform(ViewActions.scrollTo(), click())
        Thread.sleep(3000)
    }

    @Then("I should view confirmation popup")
    fun viewConfirmationPopup() {
        onView(withId(R.id.logoutConfirmationSheet)).check(matches(isDisplayed()))
    }

    @When("I click on confirm logout button")
    fun confirmLogout() {
        onView(withId(R.id.logoutConfirmationSheetConfirmButton)).perform(click())
        Thread.sleep(5000)
    }

    @Then("I should be redirected to intro screen")
    fun redirectedToLoginScreen() {
        onView(withId(R.id.introActivity)).check(matches(isDisplayed()))
    }
}

