package com.afrimax.paymaart

import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
class MalawiFullKyc {

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
    @When("I select complete kyc")
    fun iSelectCompleteKyc() {
        Espresso.onView(withId(R.id.kycProgressActivityCompleteKycButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I am redirected to kyc select screen")
    fun redirectedToKycSelectScreen() {
        Espresso.onView(withId(R.id.kycSelectActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I select malawi full kyc")
    fun iSelectMalawiFullKyc() {
        Espresso.onView(withId(R.id.kycSelectActivityMalawiBox))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycSelectActivityFullKycButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(7000)
    }

    @When("I select non malawi full kyc")
    fun iSelectNonMalawiFullKyc() {
        Espresso.onView(withId(R.id.kycSelectActivityNonMalawiContainer))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I select {string} as the nationality")
    fun iSelectTheNation(nation: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityIntlNationalityTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycNationalityActivityRV)).perform(
            RecyclerViewActions.actionOnItemAtPosition<KycNationAdapter.KycNationViewHolder>(
                0, ViewActions.click()
            )
        )
        Thread.sleep(3000)
    }


    @Then("I am redirected to KYC screen one")
    fun iAmRedirectedToKycSelectScreen() {
        Espresso.onView(withId(R.id.kycYourAddressActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Then("I am redirected to the kyc journey")
    fun redirectedToKycJourney() {
        Espresso.onView(withId(R.id.kycProgressActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Given("I am in KYC screen one")
    fun iAmInKYCScreen1() {
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.kycYourAddressActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Given("I enter the street name as {string}")
    fun iEnterStreetName(street: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameET)).perform(
            ViewActions.scrollTo(), ViewActions.typeText(street), ViewActions.closeSoftKeyboard()
        )
        Thread.sleep(5000)
    }

    @When("I enter the street name as {string} for KYC")
    fun enterStreetName(streetName: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameET)).perform(
            ViewActions.scrollTo(),
            ViewActions.typeText(streetName),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the town as {string} for KYC")
    fun enterTownName(townName: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityTownET)).perform(
            ViewActions.scrollTo(), ViewActions.typeText(townName), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the district as {string} for KYC")
    fun enterDistrictName(districtName: String) {
        Espresso.onView(withId(R.id.kycYourAddressActivityDistrictET)).perform(
            ViewActions.scrollTo(),
            ViewActions.typeText(districtName),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I click on proceed button on screen one")
    fun iClickOnProceedButtonOnScreenOne() {
        Espresso.onView(withId(R.id.kycYourAddressActivitySaveAndContinueButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I click on proceed button on screen two")
    fun iClickOnProceedButtonOnScreenTwo() {
        Espresso.onView(withId(R.id.kycYourIdentityActivitySaveAndContinueButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I click on proceed button on screen three")
    fun iClickOnProceedButtonOnScreenThree() {
        Espresso.onView(withId(R.id.kycYourInfoActivitySaveAndContinueButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I click on skip for KYC screen two")
    fun iClickOnSkipButtonOnScreenTwo() {
        Espresso.onView(withId(R.id.kycYourIdentityActivitySkipButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on skip for KYC screen three")
    fun iClickOnSkipButtonOnScreenThree() {
        Espresso.onView(withId(R.id.kycYourInfoActivitySkipButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I click on finish self kyc")
    fun iClickOnFinishForSelfKyc() {
        Espresso.onView(withId(R.id.kycProgressActivityFinishButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to KYC screen two")
    fun iAmInKYCScreen2() {
        Espresso.onView(withId(R.id.kycYourIdentityActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Then("I should be redirected to KYC screen three")
    fun iAmInKYCScreen3() {
        Espresso.onView(withId(R.id.kycYourInfoActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @When("I should see error message {string} for field with ID {string} in kyc screen")
    fun checkErrorMessageDisplayed(errorMessage: String, fieldID: String) {
        when (fieldID) {
            "streetName" -> {
                Espresso.onView(withId(R.id.kycYourAddressActivityStreetNameWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "selfieCapture" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivitySelfieStatusTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "gender" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "dateOfBirth" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityDOBWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "occupation" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityOccupationWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "bankAccountName" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityAccountNameWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }

            "bankAccountNumber" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityAccountNumberWarningTV))
                    .perform(ViewActions.scrollTo())
                    .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
            }
        }
    }

    @Then("I should see the district and town getting prefilled")
    fun districtAndTownPrefilled() {
        //Select 1st item from popup/dropdown
        Espresso.onData(CoreMatchers.instanceOf(String::class.java))
            .inRoot(RootMatchers.isPlatformPopup()).atPosition(0)
            .perform(ViewActions.click())
        Thread.sleep(5000)

        val district = getText(Espresso.onView(withId(R.id.kycYourAddressActivityDistrictET)))
        val town = getText(Espresso.onView(withId(R.id.kycYourAddressActivityTownET)))

        require(town.isNotEmpty())
        require(district.isNotEmpty())
    }

    @When("I select the gender as {string}")
    fun selectGender(gender: String) {
        when (gender) {
            "Male" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderMaleRB))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Female" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderFemaleRB))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Other" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityGenderUndisclosedRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
        }
    }

    @When("I select the date of birth as {string}")
    fun iSelectDateOfBirthAs(dateString: String) {
        // Select date and click OK
        val dateObjects = dateString.split("-")

        val year = dateObjects[2].toInt()
        val month = dateObjects[1].toInt()
        val day = dateObjects[0].toInt()

        Espresso.onView(withId(R.id.kycYourInfoActivityDOBTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker::class.java.getName())))
            .perform(
            PickerActions.setDate(year, month, day)
        )
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click())
        Thread.sleep(1000)
    }

    @When("I click on occupation source of funds")
    fun selectOccupationAndSourceFunds() {
        Espresso.onView(withId(R.id.kycYourInfoActivityOccupationTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be redirected to occupation source of funds selection screen")
    fun iShouldViewOccupationSourceFundsSelectionScreen() {
        Espresso.onView(withId(R.id.kycOccupationActivity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @When("I select the occupation type as {string}")
    fun selectOccupationType(occupationType: String) {
        when (occupationType) {
            "Employed" -> {
                Espresso.onView(withId(R.id.kycOccupationActivityEmployedCL))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
                Thread.sleep(3000)
                Espresso.onView(withId(R.id.kycOccupationEmployedSheetOption1TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }

            "Self Employed" -> {
                Espresso.onView(withId(R.id.kycOccupationActivitySelfEmployedCL)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "In Full-time Education" -> {
                Espresso.onView(withId(R.id.kycOccupationActivityEducationCL))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Seeking Employment" -> {
                Espresso.onView(withId(R.id.kycOccupationActivitySeekingCL))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Retired/Pensioner" -> {
                Espresso.onView(withId(R.id.kycOccupationActivityRetiredCL))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }

            "Others" -> {
                Espresso.onView(withId(R.id.kycOccupationActivityOtherCL))
                    .perform(ViewActions.scrollTo(), ViewActions.click())
            }
        }
    }

    @When("I click on next button")
    fun nextButton() {
        Espresso.onView(withId(R.id.kycOccupationActivityNextButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be able to view option to enter employer name")
    fun iShouldViewOptionToEnterEmployerName() {
        Espresso.onView(withId(R.id.kycYourInfoActivityEmployerNameET))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I enter the employer name as {string}")
    fun enterEmployerName(employerName: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityEmployerNameET)).perform(
            ViewActions.scrollTo(),
            ViewActions.typeText(employerName),
            ViewActions.closeSoftKeyboard()
        )
    }

    @When("I select the industry sector as {string}")
    fun selectIndustrySector(industrySector: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityIndustrySectorTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        when (industrySector) {
            "Healthcare service" -> {
                Espresso.onView(withId(R.id.kycIndustrySectorSheetOption7TV))
                    .perform(ViewActions.click())
            }
        }
        Thread.sleep(3000)
    }

    @When("I enter the district as {string}")
    fun iEnterDistrictNameAs(district: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityTownDistrictET)).perform(
            ViewActions.scrollTo(), ViewActions.typeText(district), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I select my monthly income as {string}")
    fun selectMonthlyIncome(monthlyIncome: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyIncomeTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        when (monthlyIncome) {
            "300,000.00 to 1,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption1TV))
                    .perform(ViewActions.click())
            }

            "1,000,000.00 to 2,500,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption2TV))
                    .perform(ViewActions.click())
            }

            "2,500,000.00 to 5,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption3TV))
                    .perform(ViewActions.click())
            }

            "5,000,000.00 to 10,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption4TV))
                    .perform(ViewActions.click())
            }

            "Over 10 Million MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption5TV))
                    .perform(ViewActions.click())

            }
        }
        Thread.sleep(3000)
    }

    @When("I select my monthly withdrawal as {string}")
    fun selectMonthlyWithdrawal(monthlyWithdrawal: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityMonthlyWithdrawalTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        when (monthlyWithdrawal) {

            "300,000.00 to 1,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption1TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }

            "1,000,000.00 to 2,500,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption2TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }

            "2,500,000.00 to 5,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption3TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }

            "5,000,000.00 to 10,000,000.00 MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption4TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }

            "Over 10 Million MWK" -> {
                Espresso.onView(withId(R.id.kycMonthlyIncomeWithdrawalSheetOption5TV))
                    .perform(ViewActions.click())
                Thread.sleep(3000)
            }
        }
    }

    @When("I select bank name as {string}")
    fun selectBankName(bankName: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityBankNameTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(3000)
        when (bankName) {
            "CDH Investment Bank" -> {
                Espresso.onView(withId(R.id.kycBanksSheetOption1TV)).perform(ViewActions.click())
            }
        }
        Thread.sleep(3000)
    }

    @When("I select purpose of relation {string}")
    fun selectPurposeOfRelation(relation: String) {
        when (relation) {
            "1" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityBusinessRelationShip1CB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "2" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityBusinessRelationShip2CB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "3" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityBusinessRelationShip3CB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "4" -> {
                Espresso.onView(withId(R.id.kycYourInfoActivityBusinessRelationShip4CB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
        }
    }

    @When("I enter the bank account number as {string}")
    fun iEnterBankAccountNumber(district: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityAccountNumberET)).perform(
            ViewActions.scrollTo(), ViewActions.typeText(district), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I enter the bank account name as {string}")
    fun iEnterBankAccountName(district: String) {
        Espresso.onView(withId(R.id.kycYourInfoActivityAccountNameET)).perform(
            ViewActions.scrollTo(), ViewActions.typeText(district), ViewActions.closeSoftKeyboard()
        )
    }

    @When("I select the ID document as {string}")
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

            "Passport" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocPassportTV)).perform(
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

    @When("I select the nature of permit as {string}")
    fun selectNatureOfPermit(natureOfPermit: String) {
        Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocPassportNatureOfPermitTV)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(3000)
        when (natureOfPermit) {
            "Single/Multiple Entry Visa" -> {
                Espresso.onView(withId(R.id.kycNatureOfPermitSheetOption1TV)).perform(
                    ViewActions.click()
                )
            }
        }
        Thread.sleep(3000)
    }

    @When("I enter the reference number as {string}")
    fun iEnterReferenceNumber(refNo: String) {
        Espresso.onView(withId(R.id.kycYourIdentityActivityIdDocPassportNumberOfReferencesTV)).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Thread.sleep(3000)
        Espresso.onView(withId(R.id.kycNumberOfReferencesSheetET)).perform(
            ViewActions.typeText(refNo), ViewActions.closeSoftKeyboard()
        )
        Espresso.onView(withId(R.id.kycNumberOfReferencesSheetSubmitButton)).perform(
            ViewActions.click()
        )
        Thread.sleep(3000)
    }

    @When("I capture front of the document and click on looks good")
    fun captureFrontOfTheDocument() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityFrontCaptureButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(7000) //More time required for the camera initialization
        Espresso.onView(withId(R.id.kycCaptureActivityCaptureButton)).perform(ViewActions.click())
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.kycCaptureActivityLooksGoodButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I capture back of the document and click on looks good")
    fun captureBackOfTheDocument() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityBackCaptureButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(7000) //More time required for the camera initialization
        Espresso.onView(withId(R.id.kycCaptureActivityCaptureButton)).perform(ViewActions.click())
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.kycCaptureActivityLooksGoodButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I should be able to view the preview of the document front and back")
    fun viewPreviewOfFrontAndBackDocs() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityImagePreviewFrontContainer))
            .perform(ViewActions.scrollTo())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
        Espresso.onView(withId(R.id.kycCaptureUploadActivityImagePreviewBackContainer))
            .perform(ViewActions.scrollTo())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
    }


    @Then("I click on submit button for national id")
    fun submitKYCDrivingLicence() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I click on submit button kyc document")
    fun submitDocument() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I click on biometric live selfie")
    fun clickOnBiometricLiveSelfie() {
        Espresso.onView(withId(R.id.kycYourIdentityActivitySelfieTV))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Thread.sleep(7000)
    }

    @When("I click on capture my live selfie and click on submit button")
    fun captureLiveSelfieAndSubmit() {
        Espresso.onView(withId(R.id.kycLiveSelfieActivityCaptureButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.kycLiveSelfieActivitySubmitButton)).perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @When("I select the Verification document as {string}")
    fun selectVerificationDocument(verificationDocument: String) {
        when (verificationDocument) {
            "Birth Certificate" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityVerDocBirthCertificateRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "Employer letter" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityVerDocEmployerLetterRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }

            "Drivers licence" -> {
                Espresso.onView(withId(R.id.kycYourIdentityActivityVerDocDriverLicenseRB)).perform(
                    ViewActions.scrollTo(), ViewActions.click()
                )
            }
        }
        Thread.sleep(3000)
    }

    @Then("I should be able to view the preview of the document front")
    fun viewPreviewOfFrontDoc() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivityImagePreviewFrontContainer))
            .perform(ViewActions.scrollTo())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
    }

    @When("I click on submit button for birth certificate")
    fun submitBirthCertificate() {
        Espresso.onView(withId(R.id.kycCaptureUploadActivitySubmitButton))
            .perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I should read a message stating KYC {string}")
    fun kycRegistrationSuccessfully(message: String) {
        Espresso.onView(withId(R.id.kycProgressActivityKycRegistrationChipTV))
            .perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    @When("I click on finish button")
    fun finishKYCButton() {
        Espresso.onView(withId(R.id.kycProgressActivityFinishButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @When("I open the menu, I should view the KYC status as {string} and {string}")
    fun openMenuAndCheckKYC(kycType: String, kycStatus: String) {
        Espresso.onView(withId(R.id.homeActivityMenuIcon)).perform(ViewActions.click())
        Thread.sleep(3000)

        Espresso.onView(withId(R.id.homeDrawerKycTypeTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(kycType)))
        Espresso.onView(withId(R.id.homeDrawerKycStatusTV)).perform(ViewActions.scrollTo())
            .check(ViewAssertions.matches(ViewMatchers.withText(kycStatus)))
    }
}

}