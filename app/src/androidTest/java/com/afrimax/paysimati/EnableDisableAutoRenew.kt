package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class EnableDisableAutoRenew {

    @When("I click on toggle button to {string} auto renew")
    fun iClickToggleButton(enableDisable: String) {
        when(enableDisable){
            "disable"->{
                onView(withId(R.id.buy_button_prime_switch))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
//                onView(ViewMatchers.withId(R.id.toggleButton))
//                    .check(ViewAssertions.matches(isNotChecked()))
            }
            "enable"->{
                onView(withId(R.id.buy_button_prime_switch))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
//                onView(ViewMatchers.withId(R.id.toggleButton))
//                    .check(ViewAssertions.matches(isChecked()))
            }
        }
    }

    @When("I click on close icon for Transaction Details screen")
    fun clickingOnCloseIcon(){

        onView(withId(R.id.paymentSuccessfulCloseButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)

    }

    @Then("I should see a pop up asking confirm auto renewal off")
    fun iShouldSeePopUP() {
        onView(withId(R.id.confirmAutoRenewalBottomSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on confirm button for auto renewal off")
    fun clickOnConfirmForAutoRenewOff(){
        onView(withId(R.id.confirmAutoRenewalConfirmButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should see toggle button getting disabled")
    fun iShouldSeeToggleButtonGettingDisabled() {
        onView(ViewMatchers.withId(R.id.buy_button_prime_switch))
            .check(ViewAssertions.matches(isNotChecked()))
    }

    @Then("I should see toggle button getting enabled")
    fun iShouldSeeToggleButtonGettingEnabled() {
        onView(ViewMatchers.withId(R.id.buy_button_prime_switch))
            .check(ViewAssertions.matches(isChecked()))
    }

    @Then("I should be redirected to auto renewal options screen")
    fun redirectToRenewalOptions(){

        onView(withId(R.id.purchasedMembershipPlanView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(2000)
    }

    @When("I click on {string} for auto renewal")
    fun clickOnRenewalOption(option: String){
        when(option){
            "Renew on Expiry"->{
                onView(withId(R.id.purchasedMembershipPlanRenewOnExpButton))
                    .perform(ViewActions.click())
                Thread.sleep(5000)
            }
            "Activate Auto-renew"->{
                onView(withId(R.id.purchasedMembershipActivateRenewNowButton))
                    .perform(ViewActions.click())
                Thread.sleep(5000)
            }
        }
    }

}