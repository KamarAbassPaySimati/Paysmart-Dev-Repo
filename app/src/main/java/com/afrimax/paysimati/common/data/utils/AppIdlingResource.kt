package com.afrimax.paysimati.common.data.utils

import androidx.test.espresso.IdlingResource
import javax.inject.Inject

class AppIdlingResource @Inject constructor() : IdlingResource {

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null
    private val backgroundOperations = mutableListOf<() -> Any>()

    override fun getName(): String = "AppIdlingResource"

    override fun isIdleNow(): Boolean = backgroundOperations.isEmpty()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun registerBackgroundOperation(operation: () -> Any) {
        backgroundOperations.add(operation)
        checkIdleState()
    }

    fun unregisterBackgroundOperation(operation: () -> Any) {
        backgroundOperations.remove(operation)
        checkIdleState()
    }

    private fun checkIdleState() {
        if (backgroundOperations.isEmpty()) {
            callback?.onTransitionToIdle()
        }
    }
}
