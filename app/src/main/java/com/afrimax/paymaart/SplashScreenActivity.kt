package com.afrimax.paymaart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afrimax.paymaart.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var b:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashScreenActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Update the progress bar every 20 milliseconds until it reaches 100%
        val progressHandler = Handler(Looper.getMainLooper())
        val progressRunnable = object : Runnable {
            var progressValue = 0

            override fun run() {
                if (progressValue <= 100) {
                    b.pbSplashScreenActivity.progress = progressValue
                    progressValue++
                    progressHandler.postDelayed(this, 20) // 20 milliseconds interval
                }
                //Launch the Main Activity
                if (progressValue == 100) {
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        progressHandler.post(progressRunnable)
    }
}