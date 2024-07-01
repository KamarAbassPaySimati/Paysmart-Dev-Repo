package com.afrimax.paymaart

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class ViewkycDetailsFullDefs {

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

//    @When("I click on the finish button")
//    fun clickOnFinishButton() {
//        Espresso.onView(ViewMatchers.withId(R.id.kycProgressActivityFinishButton))
//            .perform(ViewActions.click())
//        Thread.sleep(5000)
//    }

    @When("I open menu and navigate to the KYC reg details")
    fun openKycDetails() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.homeDrawerKycDetailsContainer))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)

    }

    @Then("I should be asked for the login pin")
    fun askingForLoginPin() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycPinSheet))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click on confirm authorisation")
    fun clickingForPinConfirmation() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycPinSheetViewButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should see error message {string} for view kyc")
    fun iShouldSeeErrorMessageFor2FA(errorMessage: String) {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycPinSheetETWarningTV))
            .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
    }

    @When("I close full screen preview screen")
    fun iCloseFullScreenPreviewScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.kycFullScreenPreviewActivityCloseButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(3000)
    }

    @When("I enter authorisation PIN {string} for view kyc")
    fun enterLoginPin(pin: String) {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycPinSheetET)).perform(
            ViewActions.replaceText(""), ViewActions.typeText(pin), ViewActions.closeSoftKeyboard()
        )
    }


    @When("I click on the dropdown option of {string}")
    fun clickingForDropDown(dropDown: String) {
        when (dropDown) {
            "Contact Details" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewSelfKycActivityContactDetailsBox))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Your Address" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewSelfKycActivityYourAddressBox))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Your Identity" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewSelfKycActivityYourIdentityBox))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Your Info" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewSelfKycActivityYourInfoBox))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }
        }
        Thread.sleep(3000)
    }

    @Then("I should see email and phone number")
    fun seeEmailAndPhoneNumber() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycContactDetailsEmailTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.viewKycContactDetailsPhoneNumberTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see the address details")
    fun seeAddressDetails() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourAddressMalawiAddressTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should able to see {string}")
    fun seeUploadedDocuments(docs: String) {
        when (docs) {
            "ID Documents" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycIdDocumentContainer))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }

            "Verification Document" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycVerificationDocumentContainer))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }

            "Live selfie" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycLiveSelfieTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }
        }
    }

    @When("I click on the view {string}")
    fun clickingOnViewDocument(viewDocument: String) {
        when (viewDocument) {
            "ID document" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycIdDocumentFrontTV))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "verification document" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycVerificationDocumentFrontTV))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "selfie" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewKycLiveSelfieTV))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "KYC details" -> {
                Espresso.onView(ViewMatchers.withId(R.id.viewSelfKycActivityBlur1IV))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }
        }
        Thread.sleep(3000)
    }

    @Then("I should see a preview of uploaded {string}")
    fun previewDocuments(document: String) {
        val text1 =
            getText(Espresso.onView(ViewMatchers.withId(R.id.kycFullScreenPreviewActivityFileNameTV)))
        require(text1 == document)

    }

    @Then("I should see gender and date of birth")
    fun seeGenderAndDob() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycInfoDetailsGenderTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoDobTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see occupation details")
    fun seeOccupationDetails() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoOccupationTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see monthly withdrawal and monthly income")
    fun seeMonthlyWithdrawalAndIncome() {
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoMonthlyWithdrawalTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.viewKycYourInfoMonthlyIncomeTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }



}