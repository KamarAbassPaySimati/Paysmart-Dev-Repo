package com.afrimax.paysimati



import android.content.Intent
import android.net.Uri

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@RunWith(Cucumber::class)
class ScanQRcode {

    @When("I click on Scan QR")
    fun clickOnScanQR() {
        Espresso.onView(ViewMatchers.withId(R.id.homeActivityScanQrButton))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }

    @Then("I see Scan any QR code screen")
    fun scanQRScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.zxing_barcode_scan)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @When("I scan the QR code")
    fun scanningQRCode () {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val testImageFile = File(context.cacheDir, "test_qr_code.jpg")

        val inputStream: InputStream =
            javaClass.classLoader!!.getResourceAsStream("qr_code.jpg")!!
        FileOutputStream(testImageFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        val testImageUri: Uri = Uri.fromFile(testImageFile)

        val intent = Intent().apply {
            data = testImageUri
        }

    }

    @When("I click on Cancel")
    fun clickCancel() {
        Espresso.onView(ViewMatchers.withId(R.id.totalAmountReceiptSheetCancel))
            .perform(ViewActions.click())
        Thread.sleep(5000)
    }
}