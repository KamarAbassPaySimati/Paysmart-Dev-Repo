package com.afrimax.paysimati

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.ApproveUserRequest
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.util.JwtUtil
import com.amplifyframework.core.Amplify
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RunWith(Cucumber::class)
class MembershipBannerViewDefs {

    @OptIn(DelicateCoroutinesApi::class)
    @Then("I make the user approved")
    fun iMakeUserApproved() {
        GlobalScope.launch {
            makeUserApproved()
        }
        Thread.sleep(7000)
    }

    @When("I click on the finish button")
    fun clickOnFinishButton() {
        Espresso.onView(ViewMatchers.withId(R.id.kycProgressActivityFinishButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I should be redirected to membership plans screen")
    fun redirectedToMembershipPlanScreen() {
        Thread.sleep(5000)
        Espresso.onView(ViewMatchers.withId(R.id.activityMembershipPlan))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Then("I should read {string} above the membership banner")
    fun membershipBanner(message: String) {
        Espresso.onView(ViewMatchers.withId(R.id.membershipPlansTitle))
            .check(ViewAssertions.matches(ViewMatchers.withText(message)))
    }

    private suspend fun makeUserApproved() {
        val userId = fetchPaymaartId()
        val payloadToken = JwtUtil.generateToken("ae6dfab7cde4573b9c8e19f273d0d896")
        val makeUserApprovedCall = ApiClient.apiService.approveUser(
            ApproveUserRequest(user_id = userId), "Bearer $payloadToken"
        )
        makeUserApprovedCall.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>, response: Response<DefaultResponse>
            ) {
                //
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                //
            }
        })
    }

    private suspend fun fetchPaymaartId(): String {
        return suspendCoroutine { continuation ->
            Amplify.Auth.getCurrentUser({
                continuation.resume(it.username.uppercase())
            }, {
                continuation.resume("")
            })
        }
    }

}