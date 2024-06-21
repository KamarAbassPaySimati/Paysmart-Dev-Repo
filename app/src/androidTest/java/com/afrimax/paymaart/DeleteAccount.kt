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
class DeleteAccount {

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

    @When("I open the menu")
    fun openTheMenu() {
        Espresso.onView(withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on settings and navigate to delete account")
    fun clickOnSettingsAndNavigateToDeleteAccount() {
        Espresso.onView(withId(R.id.homeDrawerSettingsTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(1000)
        Espresso.onView(withId(R.id.homeDrawerDeleteAccountContainer))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to delete account screen")
    fun deleteAccountScreen() {
        Espresso.onView(withId(R.id.deleteAccountActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should view the reasons for deleting the account")
    fun reasonsForDeletingTheAccount() {
        Espresso.onView(withId(R.id.deleteAccountActivityReason1CB))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.deleteAccountActivityReason2CB))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.deleteAccountActivityReason3CB))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.deleteAccountActivityReason4CB))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.deleteAccountActivityReason5CB))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I select the reason for deletion as {string}")
    fun selectingReasonForDeletion(reason: String) {
        when (reason) {
            "I no longer need to use e-payment services" -> {
                Espresso.onView(withId(R.id.deleteAccountActivityReason1CB))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }

    @When("I click on delete account button")
    fun clickingOnDeleteAccountButton() {
        Espresso.onView(withId(R.id.deleteAccountActivityDeleteAccountButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should see a confirmation popup for delete account")
    fun confirmationPopupForDeletion() {
        Espresso.onView(withId(R.id.deleteConfirmationSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on confirm button")
    fun clickingOnDeleteConfirmButton() {
        Espresso.onView(withId(R.id.deleteConfirmationSheetConfirmButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should read a message stating deletion request sent successfully")
    fun deletionSentSuccessfullyMessage() {
        val text = getText(Espresso.onView(withId(R.id.deleteRequestSuccessSheetSubTextTV)))
        require(text == "Please ensure you cash-out or make a pay-out before your account is deleted.")
    }
}