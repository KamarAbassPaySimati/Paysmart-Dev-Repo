package com.afrimax.paymaart.ui.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityRegisterBinding
import com.afrimax.paymaart.ui.utils.bottomsheets.GuideBottomSheet
import com.afrimax.paymaart.util.getDrawableExt
import com.afrimax.paymaart.util.getStringExt
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var isImageAdded: Boolean = false
    private lateinit var guideSheet: BottomSheetDialogFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        guideSheet = GuideBottomSheet()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        uploadImage()
        handleLayoutActivity()
        handleImageUpload()
    }

    private fun handleLayoutActivity(){
        binding.apply {
            firstNameEditText.disableError()
            middleNameEditText.disableError()
            lastNameEditText.disableError()
            emailEditText.disableError()
            phoneNumberEditText.disableError()
            questionOneEditText.disableError()
            questionTwoEditText.disableError()
            questionThreeEditText.disableError()
            questionFourEditText.disableError()
        }
        binding.termsAndConditionCheckbox.setOnClickListener {
            if (binding.termsAndConditionCheckbox.isChecked){
                resetErrorHelperText()
            }
        }
        binding.submitButton.setOnClickListener {
            handleEmptyField()
        }
        binding.guideButton.setOnClickListener {
            guideSheet.show(supportFragmentManager, GuideBottomSheet.TAG)
        }
    }

    private fun handleEmptyField(){
        val helperText = this.getStringExt(R.string.required_field)
        var count = 0
        val editTextErrorBackground = this.getDrawableExt(R.drawable.edit_text_error_background)
        binding.also {
            if (it.firstNameEditText.text.isNullOrEmpty()){
                it.firstNameTextInputLayout.helperText = helperText
                it.firstNameEditText.background = editTextErrorBackground
            }else{
                it.firstNameTextInputLayout.isHelperTextEnabled = false
            }

            if (it.middleNameEditText.text.isNullOrEmpty()){
                it.middleNameTextInputLayout.helperText = helperText
                it.middleNameEditText.background = editTextErrorBackground
            }else{
                it.middleNameTextInputLayout.isHelperTextEnabled = false
            }

            if (it.lastNameEditText.text.isNullOrEmpty()){
                it.lastNameTextInputLayout.helperText = helperText
                it.lastNameEditText.background = editTextErrorBackground
            }else {
                it.lastNameTextInputLayout.isHelperTextEnabled = false
            }

            if (it.emailEditText.text.isNullOrEmpty()){
                it.emailIdHelperText.visibility = View.VISIBLE
                it.emailIdBox.background = editTextErrorBackground
            }else{
                it.emailIdHelperText.visibility = View.GONE
            }

            if (it.phoneNumberEditText.text.isNullOrEmpty()){
                it.phoneNumberHelperText.visibility = View.VISIBLE
                it.phoneNumberBox.background = editTextErrorBackground
            }else{
                it.phoneNumberHelperText.visibility = View.GONE
            }

            if (!it.termsAndConditionCheckbox.isChecked){
                it.checkMarkHelperText.visibility = View.VISIBLE
            }else{
                it.checkMarkHelperText.visibility = View.GONE
            }

            if (!it.questionOneEditText.text.isNullOrEmpty() || !it.questionTwoEditText.text.isNullOrEmpty() || it.questionTwoEditText.text.isNullOrEmpty() || it.questionThreeEditText.text.isNullOrEmpty()){
                if (it.questionOneEditText.text.isNullOrEmpty()) {
                    it.questionOneEditText.background = editTextErrorBackground
                } else count ++
                if (it.questionTwoEditText.text.isNullOrEmpty()) {
                    it.questionTwoEditText.background = editTextErrorBackground
                } else  count ++
                if (it.questionThreeEditText.text.isNullOrEmpty()) {
                    it.questionThreeEditText.background = editTextErrorBackground
                } else  count ++
                if (it.questionFourEditText.text.isNullOrEmpty()) {
                    it.questionFourEditText.background = editTextErrorBackground
                } else  count ++
                if (count < 3) {
                    it.questionsHelperText.visibility = View.VISIBLE
                }else{
                    it.questionsHelperText.visibility = View.GONE
                }
            }

        }
    }

    private fun handleImageUpload(){
        binding.uploadPictureButton.setOnClickListener {
            if (isImageAdded){
                binding.profilePicture.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_no_image))
                binding.uploadPictureButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
                binding.profilePicture.background = ContextCompat.getDrawable(this, R.drawable.dashed_outline_background)
                selectedImageUri = null
                isImageAdded = false
            }else{
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageLauncher.launch(intent)
                isImageAdded = true
            }
        }
    }
    private fun uploadImage(){
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                selectedImageUri = data.data ?: return@registerForActivityResult
                Glide
                    .with(this)
                    .load(selectedImageUri)
                    .into(binding.profilePicture)
                binding.profilePicture.background = null
                binding.uploadPictureButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete))
            }
        }
    }

    private fun AppCompatEditText.disableError(){
        this.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                resetErrorHelperText()
            }

        })
    }

    private fun EditText.disableError(){
        this.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                resetErrorHelperText()
            }

        })
    }

    private fun resetErrorHelperText() {
        val editTextDrawable = this@RegisterActivity.getDrawableExt(R.drawable.edit_text_background)
        binding.apply {
            firstNameTextInputLayout.isHelperTextEnabled = false
            firstNameEditText.background = editTextDrawable
            middleNameTextInputLayout.isHelperTextEnabled = false
            middleNameEditText.background = editTextDrawable
            lastNameTextInputLayout.isHelperTextEnabled = false
            lastNameEditText.background = editTextDrawable
            questionsHelperText.visibility = View.GONE
            emailIdBox.background = editTextDrawable
            phoneNumberHelperText.visibility = View.GONE
            phoneNumberBox.background = editTextDrawable
            emailIdHelperText.visibility = View.GONE
            questionOneEditText.background = editTextDrawable
            questionTwoEditText.background = editTextDrawable
            questionThreeEditText.background = editTextDrawable
            questionFourEditText.background = editTextDrawable
            checkMarkHelperText.visibility = View.GONE
        }
    }
}