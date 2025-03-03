package com.afrimax.paysimati.ui.scanQr

import android.widget.ImageView
import com.afrimax.paysimati.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomCaptureActivity : CaptureActivity() {
    private lateinit var barcodeScannerView: DecoratedBarcodeView

    override fun initializeContent(): DecoratedBarcodeView {
        // Use your custom layout

        setContentView(R.layout.custom_scan_layout)

        barcodeScannerView = findViewById(R.id.zxing_barcode_scan)
        //to set the red line invisible
        barcodeScannerView.viewFinder.setLaserVisibility(false)

        findViewById<ImageView>(R.id.close_button).setOnClickListener {
            finish()
        }
        return barcodeScannerView
    }

}
