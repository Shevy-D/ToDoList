package com.shevy.todolist.analytics

import android.app.Application
import android.content.pm.PackageManager
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import com.shevy.todolist.R

class AnalyticsApplication : Application() {
    private var mTracker: Tracker? = null
    /*R.xml.app_tracker contains my Analytics code
            To use this, go to Google Analytics, and get
            your code, create a file under res/xml , and save
            your code as <string name="ga_trackingId">UX-XXXXXXXX-Y</string>
            */

    //mTracker = analytics.newTracker(R.xml.app_tracker);
    //
    @get:Synchronized
    private val defaultTracker: Tracker?
        get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this)

                /*R.xml.app_tracker contains my Analytics code
                To use this, go to Google Analytics, and get
                your code, create a file under res/xml , and save
                your code as <string name="ga_trackingId">UX-XXXXXXXX-Y</string>
                */

                //mTracker = analytics.newTracker(R.xml.app_tracker);
                mTracker = analytics.newTracker(R.xml.global_tracker)
                //
                mTracker!!.setAppName("ToDoList")
                mTracker!!.enableExceptionReporting(true)
                try {
                    mTracker!!.setAppId(packageManager.getPackageInfo(packageName, 0).versionName)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            return mTracker
        }

    fun send(screenName: Any) {
        send(screenName, ScreenViewBuilder().build())
    }

    private fun send(screenName: Any, params: Map<String, String>) {
        if (IS_ENABLED) {
            val tracker = defaultTracker
            tracker!!.setScreenName(getClassName(screenName))
            tracker.send(params)
        }
    }

    private fun getClassName(o: Any): String {
        var c: Class<*> = o.javaClass
        while (c.isAnonymousClass) {
            c = c.enclosingClass
        }
        return c.simpleName
    }

    fun send(screenName: Any, category: String?, action: String?) {
        send(screenName, EventBuilder().setCategory(category!!).setAction(action!!).build())
    }

    fun send(screenName: Any, category: String?, action: String?, label: String?) {
        send(
            screenName,
            EventBuilder().setCategory(category!!).setAction(action!!).setLabel(label!!).build()
        )
    }

    companion object {
        private const val IS_ENABLED = true
    }
}