package com.afrimax.paymaart

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class EditSelfkycDetailsFull {

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

    @Given("I am in Your Address screen")
    fun iAminYourAddressScreen() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivitySkipButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourAddressActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }


    @Given("I am in Your Identity screen")
    fun iAmInYourIdentityScreen() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivitySkipButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourAddressActivitySkipButton)).perform(ViewActions.click())
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourIdentityActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Given("I am in Your Info screen")
    fun iAmInYourInfoScreen() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivitySkipButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourAddressActivitySkipButton)).perform(ViewActions.click())
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourIdentityActivitySkipButton)).perform(ViewActions.click())
        Thread.sleep(5000)

        Espresso.onView(withId(R.id.kycYourInfoActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @When("I click on edit button for Kyc details screen")
    fun iClickEditButton() {
        Espresso.onView(withId(R.id.viewSelfKycActivityEditButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }


    @Then("I should be redirected to {string} screen")
    fun redirectingToNextScreen(screen: String) {
        when (screen) {
            "Your Address" -> {
                Espresso.onView(withId(R.id.kycYourAddressActivity))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }

            "Your Identity" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivity))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }

            "Your Info" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivity))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Thread.sleep(5000)
            }

            "personal details" -> {
                Espresso.onView(withId(R.id.kycYourPersonalDetailsActivity)).check(
                    ViewAssertions.matches(
                        ViewMatchers.isDisplayed()
                    )
                )
                Thread.sleep(5000)
            }
        }
    }

    @Then("I should see prefilled fields for personal details screen")
    fun personalDetailsPrefilled() {
        val firstName = getText(Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityFirstNameET)))
        val middleName = getText(Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityMiddleNameET)))
        val lastName = getText(Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityLastNameET)))
        val email = getText(Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailET)))
        val phoneNumber = getText(Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneET)))

        require(firstName.isNotEmpty())
        require(middleName.isNotEmpty())
        require(lastName.isNotEmpty())
        require(email.isNotEmpty())
        require(phoneNumber.isNotEmpty())
    }

    @Then("I should see prefilled fields for your address screen")
    fun addressScreenPrefilled() {
        val streetName = getText(Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameET)))
        val town = getText(Espresso.onView(withId(R.id.kycYourAddressActivityTownET)))
        val district = getText(Espresso.onView(withId(R.id.kycYourAddressActivityDistrictET)))

        require(streetName.isNotEmpty())
        require(town.isNotEmpty())
        require(district.isNotEmpty())
    }

    @Then("I should see prefilled fields for your info screen")
    fun yourInfoPrefilled() {

        val dob = getText(Espresso.onView(withId(R.id.kycYourInfoActivityDOBTV)))
        val occupation = getText(Espresso.onView(withId(R.id.kycYourInfoActivityOccupationTV)))
        val monthlyIncome = getText(Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyIncomeTV)))
        val monthlyWithdrawal = getText(Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyWithdrawalTV)))

        require(dob.isNotEmpty())
        require(occupation.isNotEmpty())
        require(monthlyIncome.isNotEmpty())
        require(monthlyWithdrawal.isNotEmpty())

    }

    @When("I update email field with {string}")
    fun iUpdateEnterEmail(email: String) {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailET)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(email),
            ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @When("I update email field with new email address")
    fun enterNewEmailAddress() {
        val firstName = "sreekuttan"
        val lastName = "j"
        val domain = "7edge.com" // Replace with your desired domain
        val randomNumber = faker.random().nextInt(10000, 99999) // Adjust range as needed

        val emailAddress = "$firstName.$lastName+$randomNumber@$domain"
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailET)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.replaceText(emailAddress),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I update phone number field with {string}")
    fun iEnterEmail(phoneNumber: String) {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneET)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(phoneNumber),
            ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @When("I update phone number field with new phone number")
    fun enterNewPhoneNumber() {
        val phoneNumber = faker.phoneNumber()
            .subscriberNumber(10) // Replace with logic to generate a valid phone number
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneET)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(phoneNumber),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click on save and continue button for editing personal details screen")
    fun iClickSaveAndContinuePersonalDetails() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivitySaveAndContinueButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I click on save and continue button for Your Info screen")
    fun iClickSaveAndContinueYourInfoScreen() {
        Espresso.onView(withId(R.id.kycYourInfoActivitySaveAndContinueButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I click on save and continue button for editing your address screen")
    fun iClickSaveAndContinueAddressScreen() {
        Espresso.onView(withId(R.id.kycYourAddressActivitySaveAndContinueButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @Then("I should see error message {string} for field with ID {string} in personal details page")
    fun checkErrorMessageIsDisplayed(errorMessage: String, fieldID: String) {

        when (fieldID) {
            "email" -> {
                Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailWarningTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "phone number" -> {
                Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneWarningTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "security answer" -> {
                Espresso.onView(withId(R.id.editKycVerificationSheetSecurityQuestionWarningTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

            "street name" -> {
                Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameWarningTV)).check(
                    ViewAssertions.matches(
                        ViewMatchers.withText(errorMessage)
                    )
                )
            }

        }
    }

    @When("I click on verify button for update phone number")
    fun clickVerifyPhoneNumber() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneVerifyButton)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I click on verify button for phone number verification")
    fun clickVerifyPhoneNumberOtpScreen() {
        Espresso.onView(withId(R.id.editKycVerificationSheetVerifyButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I click on verify button for update email address")
    fun clickVerifyEmail() {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailVerifyButton)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I click on verify button for email verification")
    fun clickVerifyEmailOtpScreen() {
        Espresso.onView(withId(R.id.editKycVerificationSheetVerifyButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I enter the OTP as {string} for email")
    fun enterOtp(otp: String) {
        Espresso.onView(withId(R.id.editKycVerificationSheetCodeET)).perform(
            ViewActions.replaceText(""),
            ViewActions.replaceText(otp),
            ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see error message {string} for email")
    fun checkErrorMessageIsDisplayed(errorMessage: String) {
        Espresso.onView(withId(R.id.editKycVerificationSheetWarningTV)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(errorMessage)
            )
        )
    }

    @When("I enter the valid OTP for email")
    fun enterEmailOTP() {
        Espresso.onView(withId(R.id.editKycVerificationSheetCodeET)).perform(
            ViewActions.replaceText(""), ViewActions.replaceText(
                "355948"
            ), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the valid OTP for phone number")
    fun enterPhoneNumberOTP() {
        Espresso.onView(withId(R.id.editKycVerificationSheetCodeET)).perform(
            ViewActions.replaceText(""), ViewActions.replaceText(
                "355948"
            ), ViewActions.closeSoftKeyboard()
        )
    }

    @Then("I should see the verify phone number button text changed to {string} for phone number in personal details screen")
    fun verifyPhoneNumberButton(expectedText: String) {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityPhoneVerifiedTV)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(expectedText)
            )
        )
    }

    @Then("I should see the verify email address button text changed to {string} for email in personal details screen")
    fun verifyEmailButton(expectedText: String) {
        Espresso.onView(withId(R.id.kycYourPersonalDetailsActivityEmailVerifiedTV)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(expectedText)
            )
        )
    }

    @When("I update the street name as {string} for your address screen")
    fun iStreetName(street: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameET)).perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText(""),
            ViewActions.typeText(street),
            ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @When("I select the ID document as {string} for editing self kyc")
    fun selectIDDocument(idDocument: String) {
        when (idDocument) {
            "National ID" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocNationalIdRBContainer)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
            "Drivers licence" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocDriverLicenseRBContainer)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
            "Refugee ID" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocRefugeeIdRBContainer)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
        }
        Thread.sleep(3000)
    }

    @When("I click on save and continue button for Identity screen")
    fun iClickSaveAndContinueIdentityScreen() {
        Espresso.onView(withId(R.id.kycYourIdentityActivitySaveAndContinueButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(5000)
    }

    @When("I update the gender field to {string} for edit self kyc")
    fun selectGender(gender: String) {
        when (gender) {
            "Male" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderMaleRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "Female" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderFemaleRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "Other" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderUndisclosedRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
        }
    }

    @When("I update Occupation to others")
    fun selectOccupationType() {
        Espresso.onView(withId(R.id.kycYourInfoActivityOccupationTV)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycOccupationActivityOtherCL)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(1000)
        Espresso.onView(withId(R.id.kycOccupationActivityOtherET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(faker.job().position()),
            ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(withId(R.id.kycOccupationActivityNextButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(3000)
    }

    @Then("I should see the district and town getting prefilled for your address screen")
    fun districtAndTownPrefilled() {
        //Select 1st item from popup/dropdown
        Espresso.onData(CoreMatchers.instanceOf(String::class.java))
            .inRoot(RootMatchers.isPlatformPopup()).atPosition(0).perform(ViewActions.click())
        Thread.sleep(5000)

        val district = getText(Espresso.onView(withId(R.id.kycYourAddressActivityDistrictET)))
        val town = getText(Espresso.onView(withId(R.id.kycYourAddressActivityTownET)))

        require(town.isNotEmpty())
        require(district.isNotEmpty())
    }

    @Then("I should be asked for security question")
    fun askingForSecurityQustion() {
        Espresso.onView(withId(R.id.editKycVerificationSheetSecurityQuestionTV))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I enter the security answer as {string} for email updation")
    fun iEnterSecurityAnswerForMail(answer: String) {
        Espresso.onView(withId(R.id.editKycVerificationSheetSecurityQuestionET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(answer),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click on recapture my live selfie and click on submit button")
    fun captureLiveSelfieAndSubmit() {
        Espresso.onView(withId(R.id.kycLiveSelfieActivityReCaptureButton)).perform(ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycLiveSelfieActivityCaptureButton)).perform(ViewActions.click())
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.kycLiveSelfieActivitySubmitButton)).perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I enter the security answer as {string} for phone number updation")
    fun iEnterSecurityAnswerForPhoneNumber(answer: String) {
        Espresso.onView(withId(R.id.editKycVerificationSheetSecurityQuestionET)).perform(
            ViewActions.replaceText(""),
            ViewActions.typeText(answer),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click remove front side of the document")
    fun removeFrontOfTheDocument() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityFrontRemoveButton)).perform(
            ViewActions.scrollTo(),
            ViewActions.click()
        )
        Thread.sleep(3000)
    }

    @When("I click remove back side of the document")
    fun removeBackOfTheDocument() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityBackRemoveButton)).perform(
            ViewActions.scrollTo(),
            ViewActions.click()
        )
        Thread.sleep(3000)
    }

}