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
class ReportMerchant {

    private var reason = ""

    @When("I click on Report Merchant button in Merchant Profile screen")
    fun clickReportMerchant() {
        Espresso.onView(withId(R.id.reportMerchantButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @And("I click on Report Merchant in Report Merchant screen")
    fun reportMerchant() {
        Espresso.onView(withId(R.id.ReportMerchantsumbit))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should read a popup message stating Please select at least one reason")
    fun readPopup() {
        Espresso.onView(withId(R.id.reportMerchantProfile))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I select Merchant Disputes")
    fun clickMerchantDisputes() {
        Espresso.onView(withId(R.id.check_merchant_disputes))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I select Privacy Concerns")
    fun clickPrivacyConcerns() {
        Espresso.onView(withId(R.id.check_privacy_concerns))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I select Others")
    fun clickOthers() {
        Espresso.onView(withId(R.id.check_others))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should get a popup stating Please Specify")
    fun getPopup() {
        Espresso.onView(withId(R.id.reportMerchantOthersSheetET ))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on Submit button")
    fun clickSubmit() {
        Espresso.onView(withId(R.id.reportMerchantOthersSheetSubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should not be able to Submit")
    fun notClickable() {
        Espresso.onView(withId(R.id.reportMerchantOthersSheetET ))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I enter {string}")
    fun enterReason(otherReason: String) {
        Espresso.onView(withId(R.id.reportMerchantOthersSheetET)).perform(ViewActions.typeText(otherReason), ViewActions.closeSoftKeyboard())
        reason = otherReason
    }

    @Then("I should see a popup stating Merchant reported successfully")
    fun confirmationPopup() {
        Espresso.onView(withId(R.id.sheetreportMerchant))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
}
