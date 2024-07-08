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
        Espresso.onView(withId(R.id.viewSelfKycActivityEditButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    //    @When("I click on edit button for Kyc details screen")
//    fun iClickOnEditButton() {
//        Espresso.onView(withId(R.id.editSimplifiedKycSheetEditButton)).perform(ViewActions.click())
//        Thread.sleep(3000)
//    }
    @Then("I should see a pop to for upgrade to full kyc")
    fun iShouldSeePopUP() {
        Espresso.onView(withId(R.id.editSimplifiedKycSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
    @When("I click on upgrade to full kyc button")
    fun iClickOnUpgradeToFullKYCButton(){
        Espresso.onView(withId(R.id.editSimplifiedKycSheetUpgradeButton)).perform(ViewActions.click())
        Thread.sleep(5000)
    }
    @Then("I should see address details to be prefilled and disabled to edit")
    fun iShouldSeeAddressDetailsPrefilled(){
        val streetName = getText(Espresso.onView(withId(R.id.onboardKycAddressActivityStreetNameET)))
        val town = getText(Espresso.onView(withId(R.id.onboardKycAddressActivityTownET)))
        val district = getText(Espresso.onView(withId(R.id.onboardKycAddressActivityDistrictET)))

        require(streetName.isNotEmpty())
        require(town.isNotEmpty())
        require(district.isNotEmpty())
    }
    @Then("I should see only income status to be editable")
    fun iShouldSeeOnlyIncomeStatusEditable(){
        Espresso.onView(withId(R.id.onboardKycPersonalActivityGenderRG)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.onboardKycPersonalActivityDOBTV)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.onboardKycPersonalActivityOccupationTV)).check(matches(not(isEnabled())))
//        Espresso.onView(withId(R.id.purposeOfRelation)).check(matches(not(isEnabled())))
        Espresso.onView(withId(R.id.onboardKycPersonalActivityMonthlyIncomeTV)).check(matches(isEnabled()))
        Espresso.onView(withId(R.id.onboardKycPersonalActivityMonthlyWithdrawalTV)).check(matches(isEnabled()))
    }
    @Then("I should see kyc is upgraded to full")
    fun iShouldSeeKYCUpgradedToFull(){
        Espresso.onView(withId(R.id.viewSelfKycActivityKycTypeTV)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Malawi Full KYC Registration")
            )
        )
    }

    @Then("I should be redirected to customer KYC personal details screen")
    fun redirectedToMembershipPlanScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.kycYourPersonalDetailsActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(2000)
    }

    @When("I select back button in Membership Screen")
    fun i_select_back_button_in_membership_screen( ) {
        Espresso.onView(withId(R.id.membershipPlansBackButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should read a message stating Submission Successful")
    fun iShouldReadMessageStatingSubmissionSuccessful() {
        Espresso.onView(ViewMatchers.withId(R.id.kycEditSuccessfulActivitySuccessTV)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @When("I click on the finish button on Edit Successful Screen")
    fun clickOnFinishButtonOnEditSuccessfulScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.kycEditSuccessfulActivityFinishButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }
}