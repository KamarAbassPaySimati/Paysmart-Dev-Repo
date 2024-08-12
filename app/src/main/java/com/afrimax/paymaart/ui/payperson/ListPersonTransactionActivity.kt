package com.afrimax.paymaart.ui.payperson

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.PayPerson
import com.afrimax.paymaart.data.model.PayPersonRequestBody
import com.afrimax.paymaart.data.model.PayPersonResponse
import com.afrimax.paymaart.databinding.ActivityListPersonTransactionBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.PayPersonListAdapter
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val REQUEST_READ_CONTACTS = 111
class ListPersonTransactionActivity : BaseActivity() {
    private lateinit var binding: ActivityListPersonTransactionBinding
    private val mContactsList = mutableListOf<PayPerson>()
    private var typeJob: Job? = null
    private var searchByPaymaartCredentials: Boolean = true
    private var phoneNumberList: MutableList<Contacts> = mutableListOf()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchText: String = ""
    private var isPaginating: Boolean = false
    private var paginationEnd: Boolean = false
    private var page: Int = 1
    private var totalListItems: Int = 0
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
        setupView()
        setupListeners()
        getRecentPersonTransactions()
    }

    private fun setupView(){
        val payPersonListAdapter = PayPersonListAdapter(mContactsList)
        payPersonListAdapter.setOnClickListener(object : PayPersonListAdapter.OnClickListener{
            override fun onClick(transaction: PayPerson) {
                val intent = Intent(this@ListPersonTransactionActivity, PersonTransactionActivity::class.java)
                intent.putExtra(Constants.PAYMAART_ID, transaction.paymaartId)
                intent.putExtra(Constants.CUSTOMER_NAME, transaction.fullName)
                intent.putExtra(Constants.PROFILE_PICTURE, transaction.profilePicture)
                startActivity(intent)
            }
        })
        binding.listPersonTransactionRV.apply {
            layoutManager = LinearLayoutManager(this@ListPersonTransactionActivity, LinearLayoutManager.VERTICAL, false)
            adapter = payPersonListAdapter
        }
        binding.listPersonTransactionRV.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating) {
                    isPaginating = true
                    if (!paginationEnd) {
                        if (searchText.isNotEmpty()){
                            paymaartUserPagination()
                        }else {
                            getRecentPersonTransactionsPagination()
                        }
                    }
                }
            }
        })
        binding.listPersonTransactionContactsIV.setOnClickListener {
            checkPermissionForContactsRequest()
            if(ContextCompat.checkSelfPermission(this@ListPersonTransactionActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                if (searchByPaymaartCredentials) {
                    binding.listPersonTransactionSearchET.hint = getString(R.string.phone_number_and_name)
                }else {
                    binding.listPersonTransactionSearchET.hint = getString(R.string.paymaart_id_and_name)
                }
            }else{
                showToast(getString(R.string.no_contacts_permission))
            }
            if (searchText.isNotEmpty()) {
                showEmptyScreen(true)
                mContactsList.clear()
                binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
            }
            searchByPaymaartCredentials = !searchByPaymaartCredentials
            binding.listPersonTransactionSearchET.text.clear()
        }

        binding.listPersonTransactionBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
                        phoneNumberList.clear()
                        searchText = text.toString()
                        if (searchByPaymaartCredentials){
                            if (searchText.isNotEmpty() && searchText.length > 4) {
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
                        }else{
                            if (searchText.isNotEmpty() && searchText.length > 4) {
                                when {
                                    searchText.toIntOrNull() != null -> searchContacts(searchText.toInt())
                                    searchText.toLongOrNull() != null -> searchContacts(searchText.toLong())
                                    else -> searchContacts(searchText)
                                }
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
                        }
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
            phoneNumberList.addAll(contactsSet)
        }
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
            phoneNumberList.addAll(contactsSet)
        }
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
            phoneNumberList.addAll(contactsSet)
        }
    }

    private fun searchForPaymaartUser() {
        coroutineScope.launch {
            showLoader()
            val idToken = fetchIdToken()
            try {
                val response = if (searchByPaymaartCredentials)
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
                        totalListItems += data.payPersonList.size
                        paginationEnd = totalListItems >= data.totalCount
                        if (!paginationEnd) {page++}
                        mContactsList.clear()
                        mContactsList.addAll(data.payPersonList)
                        if (mContactsList.isEmpty()) {
                            binding.listPersonTransactionRV.adapter?.notifyDataSetChanged()
                            showEmptyScreen(false)
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

    private fun getRecentPersonTransactions() {
        coroutineScope.launch {
            showLoader()
            val idToken = fetchIdToken()
            val recentTransactionHandler = ApiClient.apiService.getPersonRecentTransactionList(idToken, page)

            recentTransactionHandler.enqueue(object : Callback<PayPersonResponse> {
                override fun onResponse(call: Call<PayPersonResponse>, response: Response<PayPersonResponse>, ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()
                        if (data?.payPersonList.isNullOrEmpty()) {
                            showEmptyScreen(true)
                        }else{
                            totalListItems += data?.payPersonList?.size!!
                            paginationEnd = totalListItems >= data.totalCount
                            if (!paginationEnd) {page++}
                            mContactsList.clear()
                            mContactsList.addAll(data.payPersonList)
                        }
                    }
                    hideLoader()
                }
                override fun onFailure(call: Call<PayPersonResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun getRecentPersonTransactionsPagination() {
        coroutineScope.launch {
            showLoader()
            val idToken = fetchIdToken()
            val recentTransactionHandler = ApiClient.apiService.getPersonRecentTransactionList(idToken, page)

            recentTransactionHandler.enqueue(object : Callback<PayPersonResponse> {
                override fun onResponse(call: Call<PayPersonResponse>, response: Response<PayPersonResponse>, ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()
                        if (data?.payPersonList.isNullOrEmpty()) {
                            showEmptyScreen(true)
                        }else{
                            totalListItems += data?.payPersonList?.size!!
                            paginationEnd = totalListItems >= data.totalCount
                            if (!paginationEnd) {page++}
                            mContactsList.clear()
                            mContactsList.addAll(data.payPersonList)
                        }
                    }
                    hideLoader()
                }
                override fun onFailure(call: Call<PayPersonResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun paymaartUserPagination() {
        coroutineScope.launch {
            val idToken = fetchIdToken()
            try {
                val response = if (searchByPaymaartCredentials)
                    ApiClient.apiService.searchUsersByPaymaartCredentials(
                        header = idToken,
                        search = searchText
                    )
                else
                    ApiClient.apiService.searchUsersByPhoneCredentials(
                        header = idToken,
                        body = PayPersonRequestBody(phoneNumberList)
                    )
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()
                    if (data != null) {
                        val previousListSize = mContactsList.size
                        totalListItems += data.payPersonList.size
                        paginationEnd = totalListItems >= data.totalCount
                        if (!paginationEnd) {page++}
                        mContactsList.addAll(data.payPersonList)
                        binding.listPersonTransactionRV.adapter?.notifyItemRangeInserted(previousListSize, mContactsList.size)
                    }
                    isPaginating = false
                }else{
                    showEmptyScreen(false)
                }
            }catch (e: Exception){
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
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phoneNumber: String

)