package com.afrimax.paysimati.ui.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.databinding.ActivityIntroBinding
import com.afrimax.paysimati.ui.home.HomeActivity
import com.afrimax.paysimati.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false

        val version = "Version ${BuildConfig.VERSION_NAME}"
        binding.introActivityVersionTV.text = version

        // Google Sign-In config
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Handle clicks
        binding.continueWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.continueWithFacebook.setOnClickListener {
            // TODO: Facebook login
        }

        binding.continueWithEmail.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    federateWithCognito(idToken)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign in failed", e)
            }
        }
    }

    private fun federateWithCognito(idToken: String) {
        val logins = mapOf("accounts.google.com" to idToken)
        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            BuildConfig.CUSTOMER_COGNITO_IDENTITY_POOL_ID,
            Regions.fromName(BuildConfig.REGION)
        )
        credentialsProvider.logins = logins

        Thread {
            try {
                credentialsProvider.refresh()
                Log.d("Cognito", "Federated identity ID: ${credentialsProvider.identityId}")
                runOnUiThread {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e("Cognito", "Federation failed", e)
            }
        }.start()
    }
}
