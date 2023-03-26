package com.shevy.todolist.utility

import android.content.res.Resources
import com.shevy.todolist.R

/**
 * Created by avjindersinghsekhon on 9/21/15.
 */
class PreferenceKeys(resources: Resources) {
    val night_mode_pref_key: String

    init {
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key)
    }
}