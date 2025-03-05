package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MakePaymentToMerchantRequest {

    @Then("I should be able to see the payment request made by the merchant in chat screen")
    fun iSeeChatScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on Pay button for the Requested Amount 100000 in chat screen")
    fun chatScreenPay() {
        Espresso.onView(ViewMatchers.withContentDescription("chatPayButton")).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I click on Back button")
    fun clickBackButton() {
        Espresso.onView(withId(R.id.payMerchantActivityBackButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I click on Back button in Pay Merchant screen")
    fun payMerchantBackButton() {
        Espresso.onView(withId(R.id.listMerchantTransactionBackButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I again click on Back button in chat screen")
    fun chatBackButton(){
        Espresso.onView(ViewMatchers.withContentDescription("chatScreenBackButton")).perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @Given("I am on the Chat Screen")
    fun viewChatScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on Pay button for the Requested Amount 2.80 in chat screen")
    fun clickPayButton() {
        Espresso.onView(ViewMatchers.withContentDescription("chatPayButton")).perform(ViewActions.click())
        Thread.sleep(7000)
    }
}

