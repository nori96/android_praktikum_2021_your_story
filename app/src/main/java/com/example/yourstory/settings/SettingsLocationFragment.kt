package com.example.yourstory.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.yourstory.R

class SettingsLocationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.location_preferences, rootKey)
    }
}