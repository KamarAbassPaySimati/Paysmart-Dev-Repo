package com.afrimax.paymaart.common.presentation.ui.button.primary

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.presentation.utils.UiText
import com.afrimax.paymaart.databinding.ComponentPrimaryButtonBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe

class PrimaryButton @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(cxt, attrs, defStyleAttr), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var b: ComponentPrimaryButtonBinding = ComponentPrimaryButtonBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    private lateinit var vm: PrimaryButtonViewModel

    init {

        if (!isInEditMode) {
            require(cxt is ViewModelStoreOwner && cxt is LifecycleOwner)

            vm = ViewModelProvider(this)[PrimaryButtonViewModel::class.java]
            vm.observe(cxt, state = ::observeState)

            //Perform all the UI setup here

        }

        initializeWithAttributes(attrs)
    }

    private fun initializeWithAttributes(attrs: AttributeSet?) {
        attrs.let { attributes ->
            val typedArray = cxt.obtainStyledAttributes(attributes, R.styleable.PrimaryButton, 0, 0)

            val buttonText = typedArray.getString(R.styleable.PrimaryButton_buttonText)
                ?: context.getString(R.string.button)

            if (!isInEditMode) {
                vm.invoke(PrimaryButtonIntent.SetInitialData(buttonText = buttonText))
            } else {
                b.primaryButton.text = buttonText
            }

            typedArray.recycle()
        }
    }

    /**Observe changes in the State using Orbit StateFlow*/
    private fun observeState(state: PrimaryButtonState) {
        modifyButtonText(state.currentButtonText)
        modifyButtonLoader(state.showLoader)
    }


    private fun modifyButtonText(buttonText: UiText) {
        b.primaryButton.text = buttonText.asString(cxt)
    }

    private fun modifyButtonLoader(showLoader: Boolean) {
        if (showLoader) b.primaryButtonLoader.visibility = View.VISIBLE
        else b.primaryButtonLoader.visibility = View.GONE
    }

    fun setOnClickListener(listener: () -> Job?) {
        b.primaryButton.setOnClickListener {
            if (cxt is LifecycleOwner) {
                cxt.lifecycleScope.launch {
                    // Show the loader
                    vm.invoke(PrimaryButtonIntent.ChangeButtonLoaderStatus(isVisible = true))

                    if (cxt is Activity) {
                        cxt.window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }

                    // Execute the listener and await its completion
                    val job = listener()
                    job?.join()  // Wait for the API call to complete

                    if (cxt is Activity) cxt.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    // Hide the loader after execution
                    vm.invoke(PrimaryButtonIntent.ChangeButtonLoaderStatus(isVisible = false))

                }
            }
        }
    }
}