package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ChatWithMerchant {
    private var msgText = ""
    @When("I click on Send icon")
    fun clickSendIcon() {
        Espresso.onView(ViewMatchers.withContentDescription("sendmessage")).perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @Then("no message will be sent")
    fun chatScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @And("I click on {string} tab")
    fun enterMessageTab() {
        Espresso.onView(ViewMatchers.withContentDescription("textmessage")).perform(ViewActions.click())
        Thread.sleep(7000)
    }
    @And("I type {string}")
    fun typeMessage(text: String) {
        Espresso.onView(withId(R.id.listMerchantTransactionSearchET))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
        msgText = text
    }
    @Then("I should see the sent message in the screen")
    fun textScreen() {
        Espresso.onView(ViewMatchers.withContentDescription("chatscreen"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}