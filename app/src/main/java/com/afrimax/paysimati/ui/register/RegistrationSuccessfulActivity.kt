package com.afrimax.paysimati.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.ResendCredentialsRequest
import com.afrimax.paysimati.databinding.ActivityRegistrationSuccessfulBinding
import com.afrimax.paysimati.ui.login.LoginActivity
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.RecaptchaManager
import com.afrimax.paysimati.util.showLogE
import com.google.android.recaptcha.RecaptchaAction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationSuccessfulActivity : AppCompatActivity() {
    private lateinit var b: ActivityRegistrationSuccessfulBinding
    private var resendCount = 0

    @Inject
    lateinit var recaptchaManager: RecaptchaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityRegistrationSuccessfulBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrationSuccessfulActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.successGreen)

        val email = intent.getStringExtra(Constants.INTENT_DATA_EMAIL) ?: ""

        b.registrationVerificationSheetResendTV.setOnClickListener {
            lifecycleScope.launch {
                val client = recaptchaManager.getClient()
                if (client != null) {
                    client.execute(RecaptchaAction.custom("")).onSuccess {
                        resendCredentialsApi(email)
                    }.onFailure { exception ->
                        "Response".showLogE(exception.message ?: "")
                    }
                } else {
                    resendCredentialsApi(email)
                }

            }
            resendCount++
            if (resendCount >= 3) {
                b.registrationVerificationSheetResendTV.isEnabled = false
            }
        }

        b.registrationSuccessfulActivityLoginButton.setOnClickListener {
            startActivity(Intent(this@RegistrationSuccessfulActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun resendCredentialsApi(email: String) {
        val resendCredentialsCall =
            ApiClient.apiService.resendCredentials(ResendCredentialsRequest(email = email))

        resendCredentialsCall.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>, response: Response<DefaultResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    Toast.makeText(
                        this@RegistrationSuccessfulActivity, body.message, Toast.LENGTH_LONG
                    ).show()
                } else {
                    val errorResponse: DefaultResponse = Gson().fromJson(
                        response.errorBody()!!.string(),
                        object : TypeToken<DefaultResponse>() {}.type
                    )

                    Toast.makeText(
                        this@RegistrationSuccessfulActivity,
                        errorResponse.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(
                    this@RegistrationSuccessfulActivity,
                    R.string.default_error_toast,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}