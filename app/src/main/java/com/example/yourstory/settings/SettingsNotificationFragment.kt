package com.example.yourstory.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.yourstory.R

class SettingsNotificationFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notification_preferences, rootKey)
    }
}