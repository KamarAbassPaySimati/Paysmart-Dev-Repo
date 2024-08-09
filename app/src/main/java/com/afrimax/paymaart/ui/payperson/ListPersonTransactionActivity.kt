package com.afrimax.paymaart.ui.payperson

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.PayPerson
import com.afrimax.paymaart.data.model.PayPersonRequestBody
import com.afrimax.paymaart.data.model.PayPersonResponse
import com.afrimax.paymaart.databinding.ActivityListPersonTransactionBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.PayPersonListAdapter
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

private const val REQUEST_READ_CONTACTS = 111
class ListPersonTransactionActivity : BaseActivity() {
    private lateinit var binding: ActivityListPersonTransactionBinding
    private val mContactsList = mutableListOf<PayPerson>()
    private var typeJob: Job? = null
    private var searchByPaymaartCredentials: Boolean = true
    private var phoneNumberList: List<String> = emptyList()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchText: String = ""
    private var searchJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPersonTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listPersonTransactionActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        checkPermissionForContactsRequest()
        setupView()
        setupListeners()
        showEmptyScreen(true)
    }

    private fun setupView(){
        val payPersonListAdapter = PayPersonListAdapter(mContactsList)
        binding.listPersonTransactionRV.apply {
            layoutManager = LinearLayoutManager(this@ListPersonTransactionActivity, LinearLayoutManager.VERTICAL, false)
            adapter = payPersonListAdapter
        }
    }

    private fun setupListeners() {
        binding.listPersonTransactionSearchET.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                typeJob?.cancel()
                typeJob = coroutineScope.launch {
                    delay(500)
                    editable?.let { text ->
                        searchText = text.toString()
                        if (searchText.isNotEmpty()) {
                            searchForPaymaartUser()
                        } else {
                            mContactsList.clear()
                            binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
                            if(searchText.isNotEmpty()) {
                                showEmptyScreen(true)
                            }else{
                                showEmptyScreen(false)
                            }
                        }

//                        val inputText = text.toString()
//                        if (inputText.isNotEmpty()) {
//                            when {
//                                inputText.toIntOrNull() != null -> searchContacts(inputText.toInt())
//                                inputText.toLongOrNull() != null -> searchContacts(inputText.toLong())
//                                else -> searchContacts(inputText)
//                            }
//                        } else {
//                            mContactsList.clear()
//                            binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
//                        }
                    }
                }
            }
        })
    }

    private fun checkPermissionForContactsRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { } else {
                    "Response".showLogE("MainActivity: Permission denied to read contacts")
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun searchContacts(query: String) {
        val contactsSet = mutableSetOf<Contacts>()
        val contentResolver = contentResolver
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (pCursor != null && pCursor.count > 0) {
                        while (pCursor.moveToNext()) {
                            val phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactsSet.add(
                                Contacts(
                                    name = name,
                                    phoneNumber = phoneNo
                                )
                            )
                        }
                        pCursor.close()
                    }
                }
            }
            cursor.close()
        }
        mContactsList.clear()
//        mContactsList.addAll(contactsSet)
    }

    @SuppressLint("Range")
    private fun searchContacts(query: Long) {
        val contactsSet = mutableSetOf<Contacts>()
        val contentResolver = contentResolver
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                val contactCursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    "${ContactsContract.Contacts._ID} = ?",
                    arrayOf(contactId),
                    null
                )

                if (contactCursor != null && contactCursor.moveToFirst()) {
                    val name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    contactsSet.add(
                        Contacts(
                            name = name,
                            phoneNumber = phoneNo
                        )
                    )
                    contactCursor.close()
                }
            }
            cursor.close()
        }
        mContactsList.clear()
//        mContactsList.addAll(contactsSet)
    }

    @SuppressLint("Range")
    private fun searchContacts(query: Int) {
        val contactsSet = mutableSetOf<Contacts>()
        val contentResolver = contentResolver
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

                val contactCursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    "${ContactsContract.Contacts._ID} = ?",
                    arrayOf(contactId),
                    null
                )

                if (contactCursor != null && contactCursor.moveToFirst()) {
                    val name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    contactsSet.add(
                        Contacts(
                            name = name,
                            phoneNumber = phoneNo
                        )
                    )
                    contactCursor.close()
                }
            }
            cursor.close()
        }
        mContactsList.clear()
//        mContactsList.addAll(contactsSet)
    }
    private fun searchForPaymaartUser() {
        var response: Response<PayPersonResponse>
        coroutineScope.launch {
            showLoader()
            val idToken = fetchIdToken()
            try {
                response = if (searchByPaymaartCredentials)
                    ApiClient.apiService.searchUsersByPaymaartCredentials(
                        header = idToken,
                        search = searchText
                    )
                else
                    ApiClient.apiService.searchUsersByPhoneCredentials(
                        header = idToken,
                        body = PayPersonRequestBody(phoneNumberList)
                    )
                if (response.isSuccessful) {
                    hideLoader()
                    val data = response.body()
                    if (data != null) {
                        mContactsList.clear()
                        mContactsList.addAll(data.payPersonList)
                        if (mContactsList.isEmpty()) {
                            binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
                            showEmptyScreen(true)
                        }else {
                            binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
                        }
                    }
                }else{
                    hideLoader()
                    showEmptyScreen(false)
                }
            }catch (e: Exception){
                hideLoader()
                if(searchText.isEmpty()) {
                    showEmptyScreen(true)
                }else{
                    showEmptyScreen(false)
                }
                showToast(getString(R.string.default_error_toast))
            }
        }
    }

    private fun showLoader() {
        binding.listPersonTransactionLoaderLottie.visibility = View.VISIBLE
        binding.listPersonTransactionNoDataFoundContainer.visibility = View.GONE
        binding.listPersonTransactionContentBox.visibility = View.GONE
    }

    private fun hideLoader() {
        binding.listPersonTransactionLoaderLottie.visibility = View.GONE
        binding.listPersonTransactionNoDataFoundContainer.visibility = View.GONE
        binding.listPersonTransactionContentBox.visibility = View.VISIBLE
    }

    private fun showEmptyScreen(condition: Boolean) {
        // true - no past transactions
        //false - no data found when searched,
        binding.listPersonTransactionLoaderLottie.visibility = View.GONE
        binding.listPersonTransactionContentBox.visibility = View.GONE
        binding.listPersonTransactionNoDataFoundContainer.visibility = View.VISIBLE
        binding.listPersonTransactionNoDataFoundIV.setImageResource(if(condition) R.drawable.ico_search_for_users else R.drawable.ico_no_data_found)
        binding.listPersonTransactionNoDataFoundTitleTV.text = getString(if (condition) R.string.no_transactions_yet else R.string.no_data_found)
        binding.listPersonTransactionNoDataFoundSubtextTV.text = getString(if (condition) R.string.no_transactions_subtext else R.string.no_data_found_subtext)

    }
}



data class Contacts(
    val name: String,
    val phoneNumber: String

)