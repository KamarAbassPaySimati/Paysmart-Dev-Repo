package com.afrimax.paymaart

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MalawiSimplified {
    @When("I select malawi simplified kyc")
    fun iSelectMalawiSimplifiedKyc() {
        Espresso.onView(withId(R.id.kycSelectActivityMalawiBox))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycSelectActivitySimplifiedKycButton)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(7000)
    }

    @Then("I click on submit button for drivers licence")
    fun submitDrivingLicence() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I click on submit button for employer letter")
    fun submitEmployerLetter() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should view monthly income and monthly withdrawal selected as {string}")
    fun iShouldSeePreSelectedMonthlyIncome(selectedValue: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyIncomeTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(selectedValue)))
        Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyWithdrawalTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(selectedValue)))
    }
}

}