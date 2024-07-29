package com.afrimax.paymaart

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.cucumber.java.After

class ActivityScenarioHolder {

    private var scenario: ActivityScenario<*>? = null

    fun launch(intent: Intent) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        scenario = ActivityScenario.launch<Activity>(intent).onActivity { activity ->
            instrumentation.uiAutomation.grantRuntimePermission(
                activity.packageName, "android.permission.CAMERA"
            )
            instrumentation.uiAutomation.grantRuntimePermission(
                activity.packageName, "android.permission.POST_NOTIFICATIONS"
            )
        }
    }

    /**
     *  Close activity after scenario
     */
    @After
    fun close() {
        scenario?.close()
    }


}