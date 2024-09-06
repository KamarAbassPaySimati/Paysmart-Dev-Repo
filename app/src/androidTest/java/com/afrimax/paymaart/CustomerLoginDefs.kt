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
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.GetSharedSecretRequest
import com.afrimax.paymaart.data.model.GetSharedSecretResponse
import com.afrimax.paymaart.util.JwtUtil
import com.marcelkliemannel.kotlinonetimepassword.GoogleAuthenticator
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import net.datafaker.Faker
import org.hamcrest.Matcher
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

@RunWith(Cucumber::class)
class CustomerLoginDefs {
    val faker = Faker()
    private var otp: String? = null
    private var secret: String = ""
    private var phoneNumber: String = ""
    private var username = ""
    private fun generateTOTP(secret: String): String {
        val timestamp = Date(System.currentTimeMillis())
        return GoogleAuthenticator(secret).generate(timestamp)
    }


    private fun setSharedMfaSecret() {
        val payloadToken = JwtUtil.generateToken("ae6dfab7cde4573b9c8e19f273d0d896")
        val getSharedSecretCall = ApiClient.apiService.getSharedSecret(
            GetSharedSecretRequest(username), "Bearer $payloadToken"
        )
        getSharedSecretCall.enqueue(object : Callback<GetSharedSecretResponse> {
            override fun onResponse(
                call: Call<GetSharedSecretResponse>, response: Response<GetSharedSecretResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    secret = body.mfa_code
                }
            }

            override fun onFailure(call: Call<GetSharedSecretResponse>, t: Throwable) {
                //
            }
        })
    }

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


    @Given("The Intro screen is displayed")
    fun theIntroScreenIsDisplayed() {
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.intro_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I click the login customer button")
    fun clickLoginCustomerButton() {
        Espresso.onView(withId(R.id.login_button)).perform(ViewActions.click())
    }

    @When("I click on login button")
    fun clickLoginButton() {
        Espresso.onView(withId(R.id.loginActivityLoginButton)).perform(ViewActions.click())
        Thread.sleep(7000)
    }

    @Then("I see the login screen")
    fun seeLoginScreen() {
        Espresso.onView(withId(R.id.loginActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Given("The login screen is displayed")
    fun theLoginScreenIsDisplayed() {
        Thread.sleep(5000)
        Espresso.onView(withId(R.id.intro_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.login_button)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.loginActivity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @When("I enter a valid phone number for registration")
    fun enterValidPhoneNumber() {
        //BDD phone numbers start with 10
        val phoneNumber = "10" + faker.phoneNumber().subscriberNumber(8)
        Espresso.onView(withId(R.id.onboardRegistrationActivityPhoneET))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(phoneNumber),
                ViewActions.closeSoftKeyboard()
            )
        this.phoneNumber = phoneNumber
    }

    @When("I enter my previously registered phone number")
    fun enterPhoneNumber() {
        Espresso.onView(withId(R.id.loginActivityPhoneTF))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(phoneNumber),
                ViewActions.closeSoftKeyboard()
            )
        username = "+265$phoneNumber"
    }

    @When("I click on go to login")
    fun navigateToLogin() {
        Espresso.onView(withId(R.id.registrationSuccessfulActivityLoginButton)).perform(ViewActions.click())
    }

    @When("I choose to login with phone number")
    fun chooseLoginWithPhoneNumber() {
        Espresso.onView(withId(R.id.loginActivityLoginByChangeTV))
            .perform(ViewActions.click()) // Open the dropdown if not already done

        Espresso.onView(withId(R.id.dialogLoginLoginByPhoneNumber)).perform(ViewActions.click())
    }

    @When("I choose to login with email address")
    fun chooseLoginWithEmail() {
        Espresso.onView(withId(R.id.loginActivityLoginByChangeTV))
            .perform(ViewActions.click()) // Open the dropdown if not already done

        Espresso.onView(withId(R.id.dialogLoginLoginByEmail)).perform(ViewActions.click())
    }

    @When("I choose to login with paymart ID")
    fun chooseLoginWithPaymaartId() {
        Espresso.onView(withId(R.id.loginActivityLoginByChangeTV))
            .perform(ViewActions.click()) // Open the dropdown if not already done

        Espresso.onView(withId(R.id.dialogLoginLoginByPaymaartId)).perform(ViewActions.click())
    }

    @When("I enter phone number {string}")
    fun enterPhoneNumber(phoneNumber: String) {
        Espresso.onView(withId(R.id.loginActivityPhoneTF))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(phoneNumber),
                ViewActions.closeSoftKeyboard()
            )
        username = "+91$phoneNumber"
    }

    @When("I enter email address {string}")
    fun enterEmailAddress(emailAddress: String) {
        Espresso.onView(withId(R.id.loginActivityEmailET))
            .perform(ViewActions.typeText(emailAddress), ViewActions.closeSoftKeyboard())
        username = emailAddress
    }

    @When("I enter paymart ID {string}")
    fun enterPaymaartId(paymaartId: String) {
        Espresso.onView(withId(R.id.loginActivityPaymaartIdET))
            .perform(ViewActions.typeText(paymaartId), ViewActions.closeSoftKeyboard())
        username = "CMR$paymaartId"
    }

    @When("I enter login PIN {string}")
    fun enterLoginPin(pin: String) {
        Espresso.onView(withId(R.id.loginActivityPinET))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(pin),
                ViewActions.closeSoftKeyboard()
            )
    }

    @When("I enter the generated OTP")
    fun enterGeneratedOTP() {
        setSharedMfaSecret()
        Thread.sleep(5000)
        val otp: String = generateTOTP(secret)
        Espresso.onView(withId(R.id.twoFactorAuthCodeET))
            .perform(ViewActions.replaceText(otp), ViewActions.closeSoftKeyboard())
        Thread.sleep(5000)
    }

    @When("I enter the TOTP as {string}")
    fun enterAndVerifyPhoneOTP(otp: String) {
        Espresso.onView(withId(R.id.twoFactorAuthCodeET))
            .perform(ViewActions.typeText(otp), ViewActions.closeSoftKeyboard())
        Thread.sleep(5000)
    }


    @When("I should see the 2FA screen")
    fun isShouldSee2FAScreen() {
        Espresso.onView(withId(R.id.twoFactorAuth)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should see error message {string}")
    fun iShouldSeeErrorMessageFor2FA(errorMessage: String) {
        Espresso.onView(withId(R.id.twoFactorAuthCodeWarningTV))
            .check(ViewAssertions.matches(ViewMatchers.withText(errorMessage)))
    }


    @When("I copy the key and proceed")
    fun copyKeyAndProceed() {
        secret = getText(Espresso.onView(withId(R.id.twoFactorAuthCodeTV)))
        otp = generateTOTP(secret)
        println("Generated TOTP: $otp")

        Espresso.onView(withId(R.id.twoFactorAuthCopyCodeTV)).perform(ViewActions.click())
        Thread.sleep(10000)
        Espresso.onView(withId(R.id.twoFactorAuthContinueButton)).perform(ViewActions.click())
    }

    @Then("I see the TOTP screen")
    fun seeTOTPScreen() {
        Espresso.onView(withId(R.id.twoFactorAuthEnterCodeContainer)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Then("I see a message stating 2FA is enabled")
    fun see2FAEnabledMessage() {
        val expectedText =
            "Each time you log in, please enter the 6-digit code generated from Google Authenticator app"
        Espresso.onView(withId(R.id.twoFactorAuthSuccessfulSubText))
            .check(ViewAssertions.matches(ViewMatchers.withText(expectedText)))
    }

    @When("I click the finish button")
    fun clickFinishButton() {
        Espresso.onView(withId(R.id.twoFactorAuthFinishButton)).perform(ViewActions.click())
        Thread.sleep(3000)
    }

    @Then("I am redirected to the homepage")
    fun redirectedToHomePage() {
        Espresso.onView(withId(R.id.homeActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }

    @Then("I am redirected to the kyc journey")
    fun redirectedToKycJourney() {
        Espresso.onView(withId(R.id.kycProgressActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(5000)
    }
}

