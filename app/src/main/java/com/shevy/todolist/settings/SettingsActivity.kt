package com.shevy.todolist.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.shevy.todolist.R
import com.shevy.todolist.analytics.AnalyticsApplication
import com.shevy.todolist.main.MainFragment
import com.shevy.todolist.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    var app: AnalyticsApplication? = null
    override fun onResume() {
        super.onResume()
        app?.send(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as AnalyticsApplication
        val theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(
            MainFragment.THEME_SAVED,
            MainFragment.LIGHTTHEME
        )
        if (theme == MainFragment.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        super.onCreate(savedInstanceState)
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings)
        /*val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }*/
        val fm = fragmentManager
        fm.beginTransaction().replace(R.id.mycontent, SettingsFragment()).commit()
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