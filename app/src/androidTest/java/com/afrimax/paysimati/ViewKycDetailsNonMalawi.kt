package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.Then
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewKycDetailsNonMalawi {

    @Then("I should see Nationality")
    fun iShouldSeeNationality() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourAddressNationalityContainer))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see Malawi Address")
    fun viewMalawiAddressDetails() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourAddressMalawiAddressContainer))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see International Address")
    fun viewInternationalAddressDetails() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourAddressInternationalAddressContainer))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should able to see type of visa or permit")
    fun viewVisaType() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycIdDocumentVisaTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Then("I should able to see visa or permit reference number")
    fun viewVisaReferenceNumber() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycIdDocumentRefNoTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

}