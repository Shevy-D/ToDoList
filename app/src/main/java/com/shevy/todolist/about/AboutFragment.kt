package com.shevy.todolist.about

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import com.shevy.todolist.R
import com.shevy.todolist.analytics.AnalyticsApplication
import com.shevy.todolist.appDefault.AppDefaultFragment
import kotlin.Int

class AboutFragment : AppDefaultFragment() {
    private var mVersionTextView: TextView? = null
    private val appVersion = "0.1"
    private var toolbar: Toolbar? = null
    private var contactMe: TextView? = null
    private var app: AnalyticsApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity?.application as AnalyticsApplication
        app!!.send(this)
        mVersionTextView = view.findViewById<View>(R.id.aboutVersionTextView) as TextView
        mVersionTextView!!.text = String.format(
            resources.getString(R.string.app_version),
            appVersion
        )

        contactMe = view.findViewById<View>(R.id.aboutContactMe) as TextView
        contactMe!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                app!!.send(this, "Action", "Feedback")
            }
        })
    }

    @LayoutRes
    override fun layoutRes(): Int {
        return R.layout.fragment_about
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}