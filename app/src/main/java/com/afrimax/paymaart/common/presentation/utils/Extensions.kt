package com.afrimax.paymaart.common.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.fragment.app.FragmentManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.domain.utils.Errors.Network
import com.afrimax.paymaart.common.domain.utils.Errors.Prefs
import com.afrimax.paymaart.common.domain.utils.Errors.Storage
import com.afrimax.paymaart.common.domain.utils.Errors.Validation
import com.afrimax.paymaart.common.domain.utils.GenericError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

/**This extension converts all GenericErrors into their corresponding string literals.*/
fun GenericError.asUiText(): UiText {
    return when (this) {
        //Errors that can occur while performing network calls
        Network.NO_RESPONSE -> UiText.Resource(R.string.error_no_response)
        Network.NO_INTERNET -> UiText.Resource(R.string.error_no_internet)
        Network.REQUEST_TIMEOUT -> UiText.Resource(R.string.error_request_timeout)
        Network.INTERNAL_ERROR -> UiText.Resource(R.string.error_internal_error)
        Network.PAYLOAD_TOO_LARGE -> UiText.Resource(R.string.error_payload_too_large)
        Network.UNKNOWN -> UiText.Resource(R.string.error_unknown)
        Network.UNABLE_TO_UPLOAD -> UiText.Resource(R.string.unable_to_upload)
        Network.FILE_NOT_FOUND -> UiText.Resource(R.string.file_not_found)
        Network.CONFLICT_FOUND -> UiText.Resource(R.string.conflict_found)
        Network.UNAUTHORIZED -> UiText.Resource(R.string.error_un_authorized)
        Network.BAD_REQUEST -> UiText.Resource(R.string.error_bad_request)

        //Errors that can occur while performing operations on SharedPreferences
        Prefs.NO_SUCH_DATA -> UiText.Resource(R.string.error_not_data_found)
        Prefs.UNKNOWN -> UiText.Resource(R.string.error_unknown)

        //Errors that can occur while accessing local storage files
        Storage.UNABLE_TO_ACCESS_FILE -> UiText.Resource(R.string.unable_to_access_file)
        Storage.FILE_NOT_FOUND -> UiText.Resource(R.string.file_not_found)
        Storage.PERMISSION_DENIED -> UiText.Resource(R.string.permission_denied)
        Storage.IO_ERROR -> UiText.Resource(R.string.io_error)
        Storage.INSUFFICIENT_STORAGE -> UiText.Resource(R.string.insufficient_storage)
        Storage.INVALID_URI -> UiText.Resource(R.string.invalid_uri)
        Storage.UNSUPPORTED_FILE_TYPE -> UiText.Resource(R.string.unsupported_file_type)
        Storage.SECURITY_EXCEPTION -> UiText.Resource(R.string.security_exception)
        Storage.FILE_TOO_LARGE -> UiText.Resource(R.string.file_too_large)
        Storage.FILE_ALREADY_EXISTS -> UiText.Resource(R.string.file_already_exists)

        Validation.TOO_LONG -> UiText.Resource(R.string.error_too_long)
        Validation.TOO_SMALL -> UiText.Resource(R.string.error_too_small)
        Validation.BLANK -> UiText.Resource(R.string.error_blank)
        Validation.INVALID -> UiText.Resource(R.string.error_invalid)
        Validation.UNSUPPORTED_FILE -> UiText.Resource(R.string.error_unsupported_file)
        Validation.NOT_VERIFIED -> UiText.Resource(R.string.error_not_verified)

    }
}

/**This extension converts integer values into the corresponding DP (density-independent pixels) values needed for UI elements.*/
inline val Int.DP: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

/**This extension simplifies displaying Toast messages by eliminating repetitive boilerplate code.*/
fun Context.showToast(uiText: UiText, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, uiText.asString(this), duration).show()
}

/**This extension allows to display a Snackbar without redundant boilerplate code.*/
fun Context.showSnack(view: View, uiText: UiText, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, uiText.asString(this), duration).show()
}

/**This extension function simplifies the creation of ClickableSpan objects by accepting a
 * lambda function to handle click events and applying default text styling.*/
fun clickableSpan(onClick: (View) -> Unit): ClickableSpan {
    return object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClick(widget)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false  // This makes the text not underlined by default
        }
    }
}

/**This extension streamlines access to read-only data from the ViewModel's state.*/
val <STATE : Any, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.currentState: STATE
    get() = container.stateFlow.value

/**Extension function to simplify state updates in an Orbit MVI `ContainerHost`.
 * It reduces boilerplate by directly applying the state update logic.*/
inline fun <STATE : Parcelable, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.reduceState(
    crossinline update: STATE.() -> STATE
) = intent {
    reduce { state.update() }
}

/**Extension function to simplify posting side effects in an Orbit MVI `ContainerHost`.
 *  It reduces boilerplate by directly triggering the side effect.*/
inline fun <STATE : Parcelable, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.postSideEffect(
    crossinline sideEffect: () -> SIDE_EFFECT
) = intent {
    this.postSideEffect(sideEffect())
}


/**This extension streamlines the implementation of TextChangeListener for TextView elements,
 * eliminating unnecessary boilerplate code.*/
fun EditText.setOnTextChangedListener(
    afterTextChanged: (String) -> Unit = { _ -> },
    beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> },
    onTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
) {

    // Remove any existing TextWatcher
    (getTag(id) as? TextWatcher)?.let {
        removeTextChangedListener(it)
    }

    // Create a new TextWatcher
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged(s ?: "", start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s ?: "", start, before, count)
        }
    }

    // Add the new TextWatcher
    addTextChangedListener(watcher)

    // Store the TextWatcher as a tag to manage it later
    setTag(id, watcher)
}

/**This extension simplifies the initialization of a BottomSheetFragment with arguments,
 * eliminating boilerplate code and improving code clarity.*/
fun FragmentManager.showBottomSheet(
    fragment: BottomSheetDialogFragment, arguments: Bundle.() -> Unit = {}
) {
    fragment.arguments = Bundle().apply(arguments)
    fragment.show(this, fragment.javaClass.simpleName)
}

/**This extension function for the Activity class provides a simple way to hide the soft keyboard in Android.
 * It uses the InputMethodManager to request the system to hide the keyboard from the
 * currently focused view or a newly created view if none is focused.*/
fun Activity.hideKeyboard() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**This extension function simplifies setting an OnItemSelectedListener for a Spinner in Android.
 * It allows you to define behavior for both item selection and when no item is selected,
 * without having to implement the full AdapterView.OnItemSelectedListener interface manually every time.*/
fun Spinner.setOnItemSelectedListener(
    onItemSelected: (AdapterView<*>, View?, Int, Long) -> Unit,
    onNothingSelected: (AdapterView<*>) -> Unit = {}
) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View?, position: Int, id: Long
        ) {
            onItemSelected(parent, view, position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            onNothingSelected(parent)
        }
    }
}