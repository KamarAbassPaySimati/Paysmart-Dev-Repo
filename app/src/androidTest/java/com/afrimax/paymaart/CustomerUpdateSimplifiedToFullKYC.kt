package com.afrimax.paymaart

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.java.en.Given
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith
import org.hamcrest.Matchers.not
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers


@RunWith(Cucumber::class)
class CustomerUpdateSimplifiedToFullKYC {
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
    @Given("I should see the edit button enabled")
    fun iShouldSeeEditButtonEnabled(){
        Espresso.onView(withId(R.id.editKYCButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on edit button for Kyc details screen")
    fun iClickOnEditButton() {
        Espresso.onView(withId(R.id.editKycButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see a pop to for upgrade to full kyc")
    fun iShouldSeePopUP() {
        Espresso.onView(withId(R.id.editKYCPopUp))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on upgrade to full kyc button")
    fun iClickOnUpgradeToFullKYCButton(){
        Espresso.onView(withId(R.id.upgradeToFullKycButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }
    @Then("I should see address details to be prefilled and disabled to edit")
    fun iShouldSeeAddressDetailsPrefilled(){
        val streetName = getText(Espresso.onView(withId(R.id.streetNameField)))
        val town = getText(Espresso.onView(withId(R.id.townField)))
        val district = getText(Espresso.onView(withId(R.id.districtField)))

        require(streetName.isNotEmpty())
        require(town.isNotEmpty())
        require(district.isNotEmpty())
    }
    @Then("I should see only income status to be editable")
    fun iShouldSeeOnlyIncomeStatusEditable(){
        Espresso.onView(withId(R.id.genderRadioGroup)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.dob)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.occupation)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.purposeOfRelation)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.income)).check(matches(isEnabled()))
        Espresso.onView(withId(R.id.withdrawal)).check(matches(isEnabled()))
    }
    @Then("I should see kyc is upgraded to full")
    fun iShouldSeeKYCUpgradedToFull(){
        Espresso.onView(withId(R.id.kycType)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Malawi Full KYC Registration")
            )
        )
    }
}