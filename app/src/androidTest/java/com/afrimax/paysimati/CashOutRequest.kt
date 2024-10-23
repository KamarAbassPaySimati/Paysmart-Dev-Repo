package com.afrimax.paysimati

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.afrimax.paysimati.ui.utils.adapters.SearchUsersAdapter
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class CashOutRequest {

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

    @When("I click on the cash-out button")
    fun clickCashOutButton() {
        Espresso.onView(withId(R.id.homeActivityCashOutButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should be redirected to recent cash-out screen")
    fun redirectedToRecentCashOutScreen() {
        Espresso.onView(withId(R.id.selfCashOutSearchActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I search for agent with {string} for requesting cash-out")
    fun searchAgent(searchInput: String) {
        Espresso.onView(withId(R.id.selfCashOutSearchActivitySearchET)).perform(
            ViewActions.replaceText(searchInput), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @Then("I should read a message stating {string} for requesting cash-out")
    fun viewMessage(message: String) {
        Espresso.onView(withId(R.id.selfCashOutSearchActivityNoDataFoundTitleTV)).check(
            ViewAssertions.matches(ViewMatchers.withText(message))
        )
    }


    @Then("I should see a list of Agents for requesting cash-out")
    fun seeListOfAgents() {
        Espresso.onView(withId(R.id.selfCashOutSearchActivityRV)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @When("I click on first agent profile in list for requesting cash-out")
    fun clickFirstCustomer() {
        Espresso.onView(withId(R.id.selfCashOutSearchActivityRV)).perform(
            RecyclerViewActions.actionOnItemAtPosition<SearchUsersAdapter.SearchUsersViewHolder>(
                0, ViewActions.click()
            )
        )
        Thread.sleep(3000)
    }

    @Then("I should be redirected to complete cash-out screen")
    fun redirectingToCashOutPage() {
        Espresso.onView(withId(R.id.selfCashOutActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on the complete cash-out button")
    fun clickCompleteCashOutButton() {
        Espresso.onView(withId(R.id.selfCashOutActivityCompleteCashOutButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should see error message {string} for field with ID {string} for requesting cash-out")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {
        when (fieldID) {
            "Amount" -> {
                Espresso.onView(withId(R.id.selfCashOutActivityPaymentErrorTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "PIN" -> {
                Espresso.onView(withId(R.id.sendPaymentSheetAPF)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }
        }
    }

    @When("I enter amount as {string} for requesting cash-out")
    fun enterAmount(amount: String) {
        Espresso.onView(withId(R.id.selfCashOutActivityAmountET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(amount),
            ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see a popup for proceeding cash-out")
    fun popupForProceedingCAshOut() {
        Espresso.onView(withId(R.id.totalAmountReceiptBottomSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on proceed cash-out button")
    fun clickProceedCashOutButton() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be asked for authorisation pin for requesting cash-out")
    fun askingForAuthorisationPin() {
        Espresso.onView(withId(R.id.sendPaymentBottomSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I enter authorisation PIN {string} for requesting cash-out")
    fun enterAuthorisationPIN(pin: String) {
        Espresso.onView(withId(R.id.sendPaymentSheetAPF)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click on confirm button for requesting cash-out")
    fun clickConfirmCashOutButton() {
        Espresso.onView(withId(R.id.sendPaymentConfirmButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I should read a message stating {string} after cash out")
    fun paymentStatus(message: String) {
        Espresso.onView(withId(R.id.selfCashOutSearchActivityNoDataFoundSubtextTV))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

}