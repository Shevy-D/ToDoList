package com.shevy.todolist.about

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.shevy.todolist.R
import com.shevy.todolist.analytics.AnalyticsApplication
import com.shevy.todolist.appDefault.AppDefaultActivity
import com.shevy.todolist.main.MainFragment

class AboutActivity : AppDefaultActivity() {
    private val mVersionTextView: TextView? = null
    private var appVersion = "0.1"
    private var toolbar: Toolbar? = null
    private val contactMe: TextView? = null
    var theme: String? = null

    //    private UUID mId;
    //private val app: AnalyticsApplication? = null
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(
            MainFragment.THEME_SAVED,
            MainFragment.LIGHTTHEME
        )
        if (theme == MainFragment.DARKTHEME) {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_LightTheme)
        }
        super.onCreate(savedInstanceState)
        //        mId = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);
        //val backArrow: Drawable = resources.getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        //backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        try {
            val info: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            appVersion = info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(backArrow)
        }*/
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.about_layout
    }

    override fun createInitialFragment(): Fragment {
        return AboutFragment.newInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}