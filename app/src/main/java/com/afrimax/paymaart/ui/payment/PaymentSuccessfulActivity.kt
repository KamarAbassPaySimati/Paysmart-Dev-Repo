package com.afrimax.paymaart.ui.payment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.CashOutResponse
import com.afrimax.paymaart.data.model.PayAfrimaxResponse
import com.afrimax.paymaart.data.model.PayToRegisteredPersonResponse
import com.afrimax.paymaart.data.model.PayUnRegisteredPersonResponse
import com.afrimax.paymaart.data.model.SubscriptionPaymentDetails
import com.afrimax.paymaart.databinding.ActivityPaymentSuccessfulBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.formatEpochTime
import com.afrimax.paymaart.util.formatEpochTimeTwo
import com.afrimax.paymaart.util.getFormattedAmount
import java.io.File
import java.io.FileOutputStream

class PaymentSuccessfulActivity : BaseActivity() {
    private lateinit var binding: ActivityPaymentSuccessfulBinding
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private var transactionId: String = ""
    private var isFlagged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessfulBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.paymentSuccessfulActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.offWhite)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.offWhite)
        setAnimation()
        setupView()
    }

    private fun setupView() {
        when (val data = intent.parcelable<Parcelable>(Constants.SUCCESS_PAYMENT_DATA)) {
            is SubscriptionPaymentDetails -> {
                val model = CommonViewModel(
                    fromName = data.senderName,
                    fromId = data.senderId,
                    toName = data.receiverName,
                    toId = data.receiverId,
                    transactionAmount = data.transactionAmount,
                    transactionFees = data.transactionFee,
                    vat = data.vat,
                    transactionId = data.transactionId,
                    dateTime = data.createdAt,
                )
                setCommonView(model)
                binding.paymentSuccessfulMembershipContainer.visibility = View.VISIBLE
                binding.paymentSuccessfulMembership.text = getString(R.string.membership)
                binding.paymentSuccessfulMembershipValue.text = getString(
                    R.string.formatted_membership_value,
                    data.membership,
                    formatEpochTimeTwo(data.startDate),
                    formatEpochTimeTwo(data.endDate)
                )
                transactionId = data.transactionId ?: ""
            }

            is PayAfrimaxResponse -> {
                val model = CommonViewModel(
                    fromName = data.fromName,
                    fromId = data.fromPaymaartId,
                    toName = data.toName,
                    toId = data.toPaymaartId,
                    transactionAmount = data.transactionAmount,
                    transactionFees = data.tax?.toDouble(),
                    vat = data.vat?.toDouble(),
                    transactionId = data.transactionId,
                    dateTime = data.dateTime
                )
                setCommonView(model)

                binding.paymentSuccessfulToAfrimaxNameContainer.visibility = View.VISIBLE
                binding.paymentSuccessfulToAfrimaxIdContainer.visibility = View.VISIBLE
                binding.paymentSuccessfulToAfrimaxNameValue.text = data.afrimaxName
                binding.paymentSuccessfulToAfrimaxIdValue.text = data.afrimaxId
                if (!data.plan.isNullOrEmpty()) {
                    binding.paymentSuccessfulMembershipContainer.visibility = View.VISIBLE
                    binding.paymentSuccessfulMembership.text = getString(R.string.plan)
                    binding.paymentSuccessfulMembershipValue.text = data.plan
                }
                transactionId = data.transactionId ?: ""
            }

            is CashOutResponse -> {
                val model = CommonViewModel(
                    fromName = data.fromName,
                    fromId = data.fromPaymaartId,
                    toName = data.toName,
                    toId = data.toPaymaartId,
                    transactionAmount = data.transactionAmount?.toDouble(),
                    transactionFees = data.tax.toDouble(),
                    vat = data.vat.toDouble(),
                    transactionId = data.transactionId,
                    dateTime = data.dateTime
                )
                setCommonView(model)
                binding.paymentSuccessfulStatusText.text = getString(R.string.cash_out_requested)
                binding.paymentSuccessfulStatusContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.paymentScreenOrange
                    )
                )
                binding.paymentSuccessfulMembershipContainer.visibility = View.VISIBLE
                binding.paymentSuccessfulMembership.text = getString(R.string.balance)
                binding.paymentSuccessfulMembershipValue.text =
                    getString(R.string.amount_formatted, getFormattedAmount(data.balance))
                transactionId = data.transactionId ?: ""
            }

            is PayUnRegisteredPersonResponse -> {
                val model = CommonViewModel(
                    fromName = data.senderName,
                    fromId = data.senderId,
                    toName = data.receiverName,
                    toId = null,
                    toPhoneNumber = data.phoneNumber,
                    transactionAmount = data.amount?.toDouble(),
                    transactionFees = data.transactionFees?.toDouble(),
                    vat = data.vatAmount?.toDouble(),
                    transactionId = data.transactionId,
                    dateTime = data.createdAt.toLong()
                )
                setCommonView(model)
                binding.paymentSuccessfulToPhoneNumberContainer.visibility = View.VISIBLE
                binding.paymentSuccessfulToPaymaartIdContainer.visibility = View.GONE
                transactionId = data.transactionId ?: ""
                if (!data.note.isNullOrEmpty()) {
                    binding.paymentSuccessfulMembershipContainer.visibility = View.VISIBLE
                    binding.paymentSuccessfulMembershipValue.text = data.note
                }
            }


            is PayToRegisteredPersonResponse -> {
                val model = CommonViewModel(
                    fromName = data.senderName,
                    fromId = data.senderId,
                    toName = data.receiverName,
                    toId = data.receiverId,
                    transactionAmount = data.transactionAmount,
                    transactionFees = data.transactionFee,
                    vat = data.vat,
                    transactionId = data.transactionId,
                    dateTime = data.createdAt.toLong()
                )
                setCommonView(model)
                binding.paymentSuccessfulToPhoneNumberContainer.visibility = View.GONE
                binding.paymentSuccessfulToPaymaartIdContainer.visibility = View.VISIBLE
                transactionId = data.transactionId ?: ""
                if (!data.note.isNullOrEmpty()) {
                    binding.paymentSuccessfulMembershipContainer.visibility = View.VISIBLE
                    binding.paymentSuccessfulMembershipValue.text = data.note
                }

            }
        }

        nextScreenResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if ((result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) && data != null) {
                    isFlagged = data.getBooleanExtra(Constants.FLAGGED_STATUS, false)
                    if (isFlagged) makeTransactionFlagged()
                }
            }

        binding.paymentSuccessfulFlagPayment.setOnClickListener {
            nextScreenResultLauncher.launch(Intent(
                this@PaymentSuccessfulActivity, FlagTransactionActivity::class.java
            ).apply {
                putExtra(Constants.TRANSACTION_ID, transactionId)
            })
        }

        binding.paymentSuccessfulSharePayment.setOnClickListener {
            shareReceipt()
        }
        binding.paymentSuccessfulCloseButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun setCommonView(model: CommonViewModel) {
        binding.paymentSuccessfulPaymaartNameValue.text = model.fromName
        binding.paymentSuccessfulPaymaartIdValue.text = model.fromId
        binding.paymentSuccessfulToPaymaartNameValue.text = model.toName
        binding.paymentSuccessfulToPaymaartIdValue.text = model.toId
        binding.paymentSuccessfulToPhoneNumberValue.text = model.toPhoneNumber
        binding.paymentSuccessfulTxnValue.text =
            getString(R.string.amount_formatted, getFormattedAmount(model.transactionAmount))
        binding.paymentSuccessfulTxnFeeValue.text =
            getString(R.string.amount_formatted, getFormattedAmount(model.transactionFees))
        binding.paymentSuccessfulVatValue.text =
            getString(R.string.amount_formatted, getFormattedAmount(model.vat))
        binding.paymentSuccessfulTxnIdValue.text = model.transactionId
        binding.paymentSuccessfulTxnDateTimeValue.text = formatEpochTime(model.dateTime)
    }

    private fun makeTransactionFlagged() {
        binding.paymentSuccessfulFlagPayment.background =
            ContextCompat.getDrawable(this, R.drawable.bg_payment_status_button_filled)
        binding.paymentSuccessfulFlagPayment.setOnClickListener {
            showToast("This transaction is already flagged")
        }
    }

    private fun shareReceipt() {
        val bitmap = getBitmapFromView(
            binding.paymentSuccessfulScrollView,
            binding.paymentSuccessfulScrollView.getChildAt(0).height,
            binding.paymentSuccessfulScrollView.getChildAt(0).width
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, getImageToShare(bitmap))
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_SUBJECT, "")
            type = "image/png"
        }

        startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable: Drawable? = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun getImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "payment_receipt.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri =
                FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
        } catch (e: Exception) {
            //
        }
        return uri
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())
        window.enterTransition = slide
        window.returnTransition = slide
    }
}

data class CommonViewModel(
    val fromName: String?,
    val fromId: String?,
    val toName: String?,
    val toId: String?,
    val toPhoneNumber: String? = "",
    val transactionAmount: Double?,
    val transactionFees: Double?,
    val vat: Double?,
    val transactionId: String?,
    val dateTime: Long,
)