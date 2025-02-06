package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith


@RunWith(Cucumber::class)
class MakePaymentToMerchant {

    private var username = ""
    private var noteName = ""
    private var amount: String = ""


    @And("I enter paymart ID as {string}")
    fun enterPaymaartId(paymaartId: String) {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET))
            .perform(ViewActions.typeText(paymaartId), ViewActions.closeSoftKeyboard())
        username = "CMR$paymaartId"
    }

    @Then("I should read a message stating {string} in Search Merchant page")
    fun payMerchantPage() {
        Espresso.onView(withId(R.id.listMerchantTransactionActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I select the Merchant {string}")
    fun selectMerchant() {
        Espresso.onView(withId(R.id.payMerchantCard )).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should see the merchant's name, ID and recent transaction history")
    fun viewChatScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on Pay button")
    fun clickPayButton() {
        Espresso.onView(ViewMatchers.withContentDescription("PayID")).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should be directed to send payment screen")
    fun sendPaymentPage() {
        Espresso.onView(withId(R.id.PayMerchantActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on Send Payment")
    fun clickSendPayment() {
        Espresso.onView(withId(R.id.payMerchantActivitySendPaymentButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should read a message stating Please enter amount")
    fun enterAmountMessage() {
        Espresso.onView(withId(R.id.payMerchantActivityPaymentErrorTV)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I enter amount as {string} for pay to Merchant")
    fun enterAmount(totAmount: String) {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET))
            .perform(ViewActions.typeText(totAmount), ViewActions.closeSoftKeyboard())
        amount = totAmount
    }
    @And("I click on Proceed")
    fun clickOnProceed() {
        Espresso.onView(withId(R.id.totalAmountReceiptProceed)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @And("I enter Invalid PIN {string}")
    fun enterInvalidPin(pin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.sendPaymentPassword)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard())

    }

    @Then("I should read a message stating {string} in PIN field")
    fun invalidPinMessage() {
        Espresso.onView(withId(R.id.sendPaymentPasswordETWarning)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I enter Valid PIN {string}")
    fun enterValidPin(pin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.sendPaymentPassword)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard())

    }

    @Then("I should read a message stating {string} in Send Payment Screen")
    fun minimumAmount() {
        Espresso.onView(withId(R.id.payMerchantActivityPaymentErrorTV)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I enter {string} in Add note tab")
    fun noteTab(note: String) {
        Espresso.onView(withId(R.id.payMerchantActivityAddNoteET))
            .perform(ViewActions.typeText(note), ViewActions.closeSoftKeyboard())
        noteName = note
    }

    @Then("I should see the Payment Successful message along with details")
    fun successMessage() {
        Espresso.onView(withId(R.id.paymentSuccessfulScrollView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Given("I see Payment Successful message along with details")
    fun confirmationScreen() {
        Espresso.onView(withId(R.id.paymentSuccessfulScrollView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on Share icon")
    fun clickingShare() {
        Espresso.onView(withId(R.id.paymentSuccessfulSharePayment)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I should see a popup window with different shareable options")
    fun shareOptions() {
        Espresso.onView(withId(R.id.paymentSuccessfulSharePayment)).perform(ViewActions.click())
        Thread.sleep(7000)
    }
}


